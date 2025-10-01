package com.nghp.project.moneyapp.activity.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.db.local.LocalRepository
import com.nghp.project.moneyapp.db.model.ChatMessageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(private val repository: LocalRepository) : ViewModel() {
    private var _messages =
        MutableStateFlow<UiState<MutableList<ChatMessageModel>>>(UiState.Loading)
    val messages: StateFlow<UiState<MutableList<ChatMessageModel>>> get() = _messages

    fun insertMessage(messages: ChatMessageModel) {
        viewModelScope.launch {
            repository.insertMessage(messages)
        }
    }

    fun deleteMessage(text: String) {
        viewModelScope.launch {
            repository.deleteMessage(text)
        }
    }

    fun getChatMessage() {
        viewModelScope.launch {
            repository.getAllMessage().catch {
                _messages.value = UiState.Error(it.message.toString())
            }.collect { list ->
                val data = list.filterNot { it.message == "Typing..." }
                _messages.value = UiState.Success(data.toMutableList())
            }
        }
    }
}