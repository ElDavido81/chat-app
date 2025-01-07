package com.example.chat_app

import android.os.Bundle
import android.util.Log
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

class ChatFragment: Fragment() {

    private val chatViewModel: ChatViewModel by activityViewModels()
    private var chatId: String? = null
    private lateinit var messageBox: EditText
    private lateinit var chatRecyclerView: RecyclerView

    // Behöver kopplas till Firebase //
//    private val messages = mutableListOf<String>()

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
            lifecycleScope.launch {
                chatViewModel.attachChatListener(chatId!!)
            }
        } else {
            Log.e("ChatFragment", "chatId is null")
        }

        chatViewModel.chat.observe(viewLifecycleOwner) { chat ->
            // Tillgång till meddelanden här!
            // chat.messages
            // Använd recyclerView här...
        }

//        chatRecyclerView = view.findViewById(R.id.recyclerview)
//        chatRecyclerView.layoutManager = LinearLayoutManager(context)
//        chatRecyclerView.adapter = MessagesAdapter(messages)
//
//        messageBox = view.findViewById(R.id.messageBox)
//        val sendButton = view.findViewById<Button>(R.id.sendButton)
//
//        sendButton.setOnClickListener {
//            newMessage()
//        }
    }

// Behöver kopplas till Firebase //
//
//    private fun newMessage() {
//            val message = messageBox.text.toString()
//            messages.add(message)
//            chatRecyclerView.adapter?.notifyItemInserted(messages.lastIndex)
//
//    }
}