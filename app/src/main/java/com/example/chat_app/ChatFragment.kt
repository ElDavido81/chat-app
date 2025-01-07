package com.example.chat_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private val chatViewModel: ChatViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var messageBox: EditText
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageBox = view.findViewById(R.id.messageBox)
        val sendButton = view.findViewById<Button>(R.id.sendButton)
        chatRecyclerView = view.findViewById(R.id.recyclerview)
        chatRecyclerView.layoutManager = LinearLayoutManager(context)

        messagesAdapter = MessagesAdapter(chatViewModel.chat.value?.messages ?: mutableListOf())
        chatRecyclerView.adapter = messagesAdapter

        chatViewModel.chat.observe(viewLifecycleOwner) { chat ->
            val oldMessageCount = messagesAdapter.itemCount
            val newMessages = chat.messages
            if (newMessages.size > oldMessageCount) {
                messagesAdapter.messages = newMessages
                messagesAdapter.notifyItemInserted(newMessages.size - 1)
                chatRecyclerView.scrollToPosition(newMessages.size - 1)
            }
        }

        sendButton.setOnClickListener {
            newMessage()
        }
    }

    private fun newMessage() {
        val message = messageBox.text.toString()
        val chatId = chatViewModel.chat.value?.chatId ?: return
        val userId = authViewModel.user.value?.userId ?: return

        lifecycleScope.launch {
            chatViewModel.createMessage(chatId, message, userId)
            messageBox.setText("")
        }
    }
}
