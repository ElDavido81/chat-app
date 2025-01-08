package com.example.chat_app

import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ChatLogFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()

    private lateinit var chatLogRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatlog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.signOutButton).setOnClickListener {
            authViewModel.signOut()
        }

        view.findViewById<Button>(R.id.newChatButton).setOnClickListener {
            this.createChat(view)
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user?.userId != null) {
                chatViewModel.attachChatsListener(user!!.userId, user.email)
            }
            view.findViewById<TextView>(R.id.userEmail).text = user?.email ?: "Unavailable"
        }

        chatLogRecyclerView = view.findViewById(R.id.rv_chats)
        chatLogRecyclerView.layoutManager = LinearLayoutManager(context)

        chatAdapter = ChatLogAdapter(chatViewModel.chats.value!!.toMutableList())
        chatLogRecyclerView.adapter = chatAdapter

        chatViewModel.chats.observe(viewLifecycleOwner) { chat ->
            val oldChatsCount = chatAdapter.itemCount
            val newChats = chat
            if (newChats.size > oldChatsCount) {
                chatAdapter.chats = newChats
                chatAdapter.notifyItemInserted((newChats.size - 1) ?: 0)
                chatLogRecyclerView.scrollToPosition((newChats.size - 1) ?: 0)
            }
        }



    }

    fun createChat(view: View) {
            val receiverEmail = view.findViewById<TextInputEditText>(R.id.friendEmailEditText).text.toString()
            if (receiverEmail.isEmpty()) {
                val toast = Toast.makeText(context, "Please enter a valid email.", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else {
                lifecycleScope.launch {
                    chatViewModel.createConversation(authViewModel.user.value!!.email, authViewModel.user.value!!.userId, receiverEmail) { status, chatId ->
                        when (status) {
                            CreateChatStatus.CHATEXISTS -> {
                                val toast = Toast.makeText(context, "Chat already exists.", Toast.LENGTH_LONG)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            CreateChatStatus.USERNOTFOUND -> {
                                val toast = Toast.makeText(context, "User not found. Check for typo.", Toast.LENGTH_LONG)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            CreateChatStatus.FAILURE -> {
                                val toast = Toast.makeText(context, "Failed to create chat. Try again later.", Toast.LENGTH_LONG)
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                            CreateChatStatus.SUCCESS -> {
                                Log.d("!!!!", "Success")
                                val chatFragment = ChatFragment().apply {
                                    arguments = Bundle().apply {
                                        putString("chatId", chatId)
                                    }
                                }
                                parentFragmentManager.beginTransaction().apply {
                                    replace(R.id.main_container, chatFragment)
                                    addToBackStack("chatlogfragment")
                                    commit()
                                }
                            }
                        }
                    }
                }
            }
    }
}