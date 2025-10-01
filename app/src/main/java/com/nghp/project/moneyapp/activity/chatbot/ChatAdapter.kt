package com.nghp.project.moneyapp.activity.chatbot// com.example.demo_chatbot.ChatAdapter.kt
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.db.model.ChatMessageModel
import com.nghp.project.moneyapp.db.model.TransactionItem
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class ChatAdapter @Inject constructor(@ActivityContext private val context: Context) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var messages = mutableListOf<ChatMessageModel>()

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    fun setData(lst: MutableList<ChatMessageModel>) {
        this.messages = lst
        changeNotify()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER)
            R.layout.item_message_user
        else R.layout.item_message_ai

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessageModel) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(chatMessage: ChatMessageModel) {
            messageText.text = chatMessage.message
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}
