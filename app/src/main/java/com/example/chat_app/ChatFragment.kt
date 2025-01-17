package com.example.chat_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private val chatViewModel: ChatViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private var chatId: String? = null
    private lateinit var messageBox: TextInputEditText
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
        chatId = arguments?.getString("chatId")

        if (chatId != null) {
            Log.d("!!!!useridinchat", authViewModel.user.value!!.email)
            lifecycleScope.launch {
                chatViewModel.resetChat()
                chatViewModel.attachChatListener(chatId!!, authViewModel.user.value!!.email)
            }
        } else {
            Log.e("ChatFragment", "chatId is null")
        }
        
        messageBox = view.findViewById(R.id.messageBox)
        val sendButton = view.findViewById<ImageView>(R.id.sendButton)
        chatRecyclerView = view.findViewById(R.id.recyclerview)

        val layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
            reverseLayout = false
        }

        chatRecyclerView.layoutManager = layoutManager

        messagesAdapter = MessagesAdapter(chatViewModel.chat.value?.messages ?: mutableListOf())
        chatRecyclerView.adapter = messagesAdapter

        chatViewModel.chat.observe(viewLifecycleOwner) { chat ->
            val oldMessageCount = messagesAdapter.itemCount
            val newMessages = chat?.messages ?: mutableListOf()
            view.findViewById<TextView>(R.id.friendNameTextView).text = chat?.chatName ?: ""
            if (newMessages.size > oldMessageCount) {
                messagesAdapter.messages = newMessages
                messagesAdapter.notifyItemInserted((newMessages.size - 1) ?: 0)
                chatRecyclerView.scrollToPosition((newMessages.size - 1) ?: 0)
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
            chatViewModel.createMessage(chatId, message, userId, authViewModel.user.value!!.name)
            messageBox.setText("")
        }
    }
}

