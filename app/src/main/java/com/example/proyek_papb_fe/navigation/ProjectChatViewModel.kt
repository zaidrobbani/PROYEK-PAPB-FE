package com.example.proyek_papb_fe.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyek_papb_fe.core.data.remote.userMessage
import com.example.proyek_papb_fe.core.data.repository.ChatRepository
import com.example.proyek_papb_fe.core.data.repository.WorkflowRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface ProjectChatEvent {
    data class Open(val conversationId: String, val asUmkm: Boolean) : ProjectChatEvent
    data class Failed(val message: String) : ProjectChatEvent
}

class ProjectChatViewModel(
    private val workflowRepository: WorkflowRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _events = Channel<ProjectChatEvent>(Channel.BUFFERED)
    val events: Flow<ProjectChatEvent> = _events.receiveAsFlow()

    private var busy = false

    fun openForProject(projectId: String) {
        if (busy) return
        busy = true
        viewModelScope.launch {
            try {
                val detail = workflowRepository.detail(projectId)
                val asUmkm = detail.myRole == "UMKM"
                val counterpartId = if (asUmkm) detail.freelancer?.id else detail.umkm.id
                if (counterpartId == null) {
                    _events.send(ProjectChatEvent.Failed("Belum ada freelancer untuk project ini."))
                } else {
                    val conversation = chatRepository.openConversation(counterpartId, projectId)
                    _events.send(ProjectChatEvent.Open(conversation.id, asUmkm))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(ProjectChatEvent.Failed(e.userMessage()))
            } finally {
                busy = false
            }
        }
    }

    companion object {
        fun factory(workflowRepository: WorkflowRepository, chatRepository: ChatRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ProjectChatViewModel(workflowRepository, chatRepository) }
            }
    }
}
