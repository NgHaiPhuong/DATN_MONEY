package com.nghp.project.moneyapp.activity.chatbot

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.databinding.ActivityChatBotBinding
import com.nghp.project.moneyapp.db.model.ChatMessageModel
import com.nghp.project.moneyapp.service.remote.GeminiHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatBotActivity : BaseActivity<ActivityChatBotBinding>(ActivityChatBotBinding::inflate) {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var messages = mutableListOf<ChatMessageModel>()

    @Inject lateinit var geminiHelper: GeminiHelper

    @Inject lateinit var chatAdapter: ChatAdapter

    @Inject
    lateinit var chatViewModel: ChatViewModel

    override fun isHideNavigation(): Boolean = true

    var isSetData = false

    override fun setUp() {
        setUpLayout()
        evenClick()
    }

    private fun setUpLayout() {
        geminiHelper = GeminiHelper(this.applicationContext)

        binding.inputText.requestFocus()

        lifecycleScope.launch {
            chatViewModel.getChatMessage()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    chatViewModel.messages.collect { state ->
                        when (state) {
                            is UiState.Loading -> {}
                            is UiState.Error -> {}
                            is UiState.Success -> {
                                if(!isSetData){
                                    messages = state.data
                                    chatAdapter.setData(state.data)
                                    isSetData = true
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatBotActivity)
            adapter = chatAdapter
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    binding.recyclerView.post {
                        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    private fun evenClick() {
        binding.sendButton.setOnClickListener {
            val userText = binding.inputText.text.toString().trim()
            if (userText.isNotEmpty()) {
                addMessage(userText, isUser = true)
                binding.inputText.text?.clear()
                addMessage("Typing...", isUser = false, isPlaceholder = true)

                // gọi Gemini
                uiScope.launch {
                    val reply = geminiHelper.generateText(userText)
                    // remove typing placeholder (cuối danh sách)
                    removePlaceholderIfAny()
                    addMessage(reply, isUser = false)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }

    private fun addMessage(text: String, isUser: Boolean, isPlaceholder: Boolean = false) {
        val message = ChatMessageModel(message = text, isUser = isUser)
        messages.add(message)
        chatViewModel.insertMessage(message)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun removePlaceholderIfAny() {
        if (messages.isNotEmpty()) {
            val last = messages.last()
            if (!last.isUser && last.message == "Typing...") {
                val idx = messages.size - 1
                messages.removeAt(idx)
                chatAdapter.notifyItemRemoved(idx)
            }
        }
    }
}