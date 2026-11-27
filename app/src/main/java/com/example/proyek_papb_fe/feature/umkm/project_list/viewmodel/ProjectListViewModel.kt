package com.example.proyek_papb_fe.feature.umkm.project_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject
import com.example.proyek_papb_fe.feature.umkm.data.needsUmkmAction
import com.example.proyek_papb_fe.feature.umkm.data.toUmkmProject
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectListUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val projects: List<UmkmProject> = emptyList(),
    /** Id project yang menunggu tindakan UMKM (review hasil / pilih pelamar). */
    val actionProjectIds: Set<String> = emptySet(),
    val isDeleting: Boolean = false,
    val message: String? = null
)

class ProjectListViewModel(
    private val workflowRepository: WorkflowRepository,
    private val projectRepository: ProjectRepository,
    private val masterDataRepository: MasterDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    fun load() {
        fetch(showLoading = true)
    }

    /** Tarik untuk muat ulang: konten lama tetap tampil selama memuat. */
    fun refresh(): Job = fetch(showLoading = false)

    private fun fetch(showLoading: Boolean): Job {
        return viewModelScope.launch {
            _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
            try {
                val items = workflowRepository.myProjects().items
                val categories = runCatching { masterDataRepository.categories() }.getOrDefault(emptyList())
                    .associate { it.id to it.name }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        projects = items.map { item ->
                            item.toUmkmProject(categories[item.project.categoryId] ?: item.project.categoryId)
                        },
                        actionProjectIds = items.filter { item -> item.needsUmkmAction() }.map { item -> item.project.id }.toSet()
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
            }
        }
    }

    fun delete(projectId: String) {
        if (_uiState.value.isDeleting) return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            try {
                projectRepository.delete(projectId)
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        projects = it.projects.filterNot { p -> p.id == projectId },
                        actionProjectIds = it.actionProjectIds - projectId,
                        message = "Project berhasil dihapus"
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isDeleting = false, message = e.userMessage()) }
            }
        }
    }

    fun onMessageShown() = _uiState.update { it.copy(message = null) }

    companion object {
        fun factory(
            workflowRepository: WorkflowRepository,
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProjectListViewModel(workflowRepository, projectRepository, masterDataRepository) }
        }
    }
}
