package com.example.proyek_papb_fe.feature.freelancer.searchproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.MasterDataRepository
import com.example.proyek_papb_fe.core.data.repository.NotificationRepository
import com.example.proyek_papb_fe.core.data.repository.ProjectRepository
import com.example.proyek_papb_fe.feature.freelancer.data.toFreelancerProject
import com.example.proyek_papb_fe.feature.freelancer.searchproject.component.SearchProject
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchProjectUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val projects: List<SearchProject> = emptyList(),
    val hasUnreadNotification: Boolean = false
)

/** Cari project TERBUKA: pencarian dikirim ke BE (`search`) dengan debounce, seperti pola modul 8. */
class SearchProjectViewModel(
    private val projectRepository: ProjectRepository,
    private val masterDataRepository: MasterDataRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchProjectUiState())
    val uiState: StateFlow<SearchProjectUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        observeQuery()
        viewModelScope.launch {
            val unread = runCatching { notificationRepository.unreadCount() }.getOrDefault(0)
            _uiState.update { it.copy(hasUnreadNotification = unread > 0) }
        }
    }

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun retry() {
        viewModelScope.launch { fetch(_query.value.trim()) }
    }

    /** Tarik untuk muat ulang: daftar lama tetap tampil selama memuat. */
    fun refresh(): Job = viewModelScope.launch { fetch(_query.value.trim(), showLoading = false) }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _query.debounce(400).map { it.trim() }.distinctUntilChanged().collectLatest { fetch(it) }
        }
    }

    private suspend fun fetch(keyword: String, showLoading: Boolean = true) {
        _uiState.update { it.copy(isLoading = showLoading || it.isLoading, errorMessage = null) }
        try {
            val categories = runCatching { masterDataRepository.categories() }.getOrDefault(emptyList()).associate { it.id to it.name }
            val matches = runCatching { projectRepository.recommendedProjects(limit = 100) }.getOrDefault(emptyList())
                .associate { it.projectId to it.match }
            val page = projectRepository.searchOpen(search = keyword, limit = 100)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    projects = page.items.map { project ->
                        val ui = project.toFreelancerProject(
                            categoryName = categories[project.categoryId] ?: project.categoryId,
                            match = matches[project.id]
                        )
                        SearchProject(
                            id = ui.id,
                            title = ui.title,
                            category = ui.category,
                            tagLabel = ui.tagLabel,
                            clientName = ui.clientName,
                            clientInitials = ui.clientInitials,
                            clientLocation = ui.clientLocation,
                            budgetLabel = ui.budgetLabel,
                            budget = ui.budget,
                            deadlineLabel = ui.deadlineLabel,
                            deadline = ui.deadline,
                            match = ui.match,
                            avatarTone = ui.avatarTone,
                            isNew = ui.isNew,
                            isClientVerified = ui.isClientVerified
                        )
                    }
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = e.userMessage()) }
        }
    }

    companion object {
        fun factory(
            projectRepository: ProjectRepository,
            masterDataRepository: MasterDataRepository,
            notificationRepository: NotificationRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer { SearchProjectViewModel(projectRepository, masterDataRepository, notificationRepository) }
        }
    }
}
