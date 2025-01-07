package com.example.chat_app

import android.os.Bundle
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
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ChatLogFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val chatViewModel: ChatViewModel by activityViewModels()

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
            val receiverEmail = view.findViewById<TextInputEditText>(R.id.friendEmailEditText).text.toString()
            if (receiverEmail.isEmpty()) {
                val toast =
                    Toast.makeText(context, "Please enter a valid email.", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            lifecycleScope.launch {
                chatViewModel.createConversation(authViewModel.user.value!!.userId, receiverEmail) { status ->
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
                        }
                    }
                }
            }
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            view.findViewById<TextView>(R.id.userEmail).text = user?.email ?: "Unavailable"
        }
    }
}