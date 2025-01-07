package com.example.chat_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatFragment: Fragment() {

    private lateinit var messageBox: EditText
    private lateinit var chatRecyclerView: RecyclerView

    // Behöver kopplas till Firebase //
    private val messages = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRecyclerView = view.findViewById(R.id.recyclerview)
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        chatRecyclerView.adapter = ChatAdapter(messages)

        messageBox = view.findViewById(R.id.messageBox)
        val sendButton = view.findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            newMessage()
        }

    }

// Behöver kopplas till Firebase //

    private fun newMessage() {
            val message = messageBox.text.toString()
            messages.add(message)
            chatRecyclerView.adapter?.notifyItemInserted(messages.lastIndex)

    }

}