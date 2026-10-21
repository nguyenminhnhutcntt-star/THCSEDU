package com.example.thcsedu.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.TinNhanEntity
import com.example.thcsedu.domain.repository.TinNhanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<TinNhanEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val tinNhanRepository: TinNhanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadMessages(currentUserId: String, peerId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            tinNhanRepository.getMessagesBetween(currentUserId, peerId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading chat") }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, messages = list) }
                }
        }
    }

    fun sendMessage(senderId: String, receiverId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            try {
                val message = TinNhanEntity(
                    senderId = senderId,
                    receiverId = receiverId,
                    messageText = text.trim()
                )
                tinNhanRepository.sendMessage(message)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Failed to send message") }
            }
        }
    }
}
