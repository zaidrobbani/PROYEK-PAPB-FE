package com.example.proyek_papb_fe.feature.umkm.create_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.ApiException
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.domain.model.Category
import com.example.proyek_papb_fe.core.domain.model.ProjectInput
import com.example.proyek_papb_fe.core.domain.model.Skill
import com.example.proyek_papb_fe.core.domain.model.UploadFile
import com.example.proyek_papb_fe.core.util.DateTimeParser
import com.example.proyek_papb_fe.feature.umkm.create_project.container.CreateProjectDraft
import com.example.proyek_papb_fe.feature.umkm.data.ProjectAttachment
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import com.example.proyek_papb_fe.feature.umkm.data.projectStatusOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateProjectUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val categories: List<Category> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val source: UmkmProject? = null,
    val isSubmitting: Boolean = false,
    val fieldErrors: Map<String, String> = emptyMap(),
    val message: String? = null,
    val savedProjectId: String? = null
)

class CreateProjectViewModel(
    private val projectRepository: ProjectRepository,
    private val masterDataRepository: MasterDataRepository,
    private val editProjectId: String?,
    private val copyFromProjectId: String?
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProjectUiState())
    val uiState: StateFlow<CreateProjectUiState> = _uiState.asStateFlow()

    private var originalServerAttachmentIds: Set<String> = emptySet()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            try {
                coroutineScope {
                    val categories = async { masterDataRepository.categories() }
                    val skills = async { masterDataRepository.skills() }
                    val sourceId = editProjectId ?: copyFromProjectId
                    val source = sourceId?.let { id -> async { loadSource(id, categories.await(), skills.await()) } }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories.await(),
                            skills = skills.await(),
                            source = source?.await()
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, loadError = e.userMessage()) }
            }
        }
    }

    private suspend fun loadSource(projectId: String, categories: List<Category>, skills: List<Skill>): UmkmProject =
        coroutineScope {
            val project = projectRepository.detail(projectId)
            val attachments = async {
                if (projectId != editProjectId) emptyList()
                else runCatching { projectRepository.attachments(projectId) }.getOrDefault(emptyList())
            }
            val skillIds = runCatching {
                projectRepository.recommendedFreelancers(projectId).firstOrNull()?.match
                    ?.let { it.matchedSkillIds + it.missingSkillIds }
            }.getOrNull().orEmpty()
            val skillNames = skills.associate { it.id to it.name }
            val files = attachments.await()
            originalServerAttachmentIds = files.map { it.id }.toSet()
            UmkmProject(
                id = project.id,
                category = categories.firstOrNull { it.id == project.categoryId }?.name ?: project.categoryId,
                title = project.title,
                description = project.description,
                skills = skillIds.mapNotNull { skillNames[it] }.distinct(),
                attachments = files.map { ProjectAttachment("$ServerUriPrefix${it.id}", it.fileName, it.sizeBytes) },
                status = projectStatusOf(project.status),
                budget = project.budget,
                deadlineUtcMillis = DateTimeParser.parseDateUtcMillis(project.deadline)
            )
        }

    fun findSkillName(raw: String): String? =
        _uiState.value.skills.firstOrNull { it.name.equals(raw.trim(), ignoreCase = true) }?.name

    fun showMessage(message: String) = _uiState.update { it.copy(message = message) }

    fun submit(draft: CreateProjectDraft) {
        if (_uiState.value.isSubmitting) return
        val state = _uiState.value
        val categoryId = state.categories.firstOrNull { it.name == draft.category }?.id
        val skillIds = draft.skills.mapNotNull { name -> state.skills.firstOrNull { it.name == name }?.id }
        if (categoryId == null) {
            _uiState.update { it.copy(fieldErrors = mapOf("category" to "Pilih kategori project.")) }
            return
        }
        val input = ProjectInput(
            categoryId = categoryId,
            title = draft.title,
            description = draft.description,
            budget = draft.budget,
            deadline = DateTimeParser.formatDateUtc(draft.deadlineUtcMillis),
            skillIds = skillIds
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, fieldErrors = emptyMap(), message = null) }
            try {
                val project = if (editProjectId != null) {
                    projectRepository.update(editProjectId, input)
                } else {
                    projectRepository.create(input)
                }
                val failedUploads = syncAttachments(project.id, draft.attachments)
                val message = when {
                    failedUploads.isNotEmpty() ->
                        "Project tersimpan, tetapi lampiran gagal diunggah: ${failedUploads.joinToString()}"
                    editProjectId != null -> "Perubahan project disimpan"
                    else -> "Project berhasil dipublikasikan"
                }
                _uiState.update { it.copy(isSubmitting = false, message = message, savedProjectId = project.id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val fieldErrors = (e as? ApiException)?.fieldErrors.orEmpty().mapKeys { (key, _) ->
                    when (key) {
                        "category_id" -> "category"
                        "skill_ids" -> "skills"
                        else -> key
                    }
                }
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        fieldErrors = fieldErrors,
                        message = if (fieldErrors.isEmpty()) e.userMessage() else null
                    )
                }
            }
        }
    }

    private suspend fun syncAttachments(projectId: String, attachments: List<ProjectAttachment>): List<String> {
        val keptServerIds = attachments.filter { it.uri.startsWith(ServerUriPrefix) }
            .map { it.uri.removePrefix(ServerUriPrefix) }.toSet()
        (originalServerAttachmentIds - keptServerIds).forEach { id ->
            runCatching { projectRepository.deleteAttachment(projectId, id) }
        }
        return attachments.filterNot { it.uri.startsWith(ServerUriPrefix) }.mapNotNull { attachment ->
            val uploaded = runCatching {
                projectRepository.uploadAttachment(projectId, UploadFile(attachment.uri, attachment.name, null))
            }
            if (uploaded.isSuccess) null else attachment.name
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    fun onSavedHandled() = _uiState.update { it.copy(savedProjectId = null) }

    companion object {
        const val ServerUriPrefix = "server:"

        fun factory(
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository,
            editProjectId: String?,
            copyFromProjectId: String?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { CreateProjectViewModel(projectRepository, masterDataRepository, editProjectId, copyFromProjectId) }
        }
    }
}
