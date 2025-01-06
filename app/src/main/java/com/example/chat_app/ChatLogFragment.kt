package com.example.chat_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels

class ChatLogFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()

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

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            view.findViewById<TextView>(R.id.userEmail).text = user?.email ?: "Unavailable"
        }
    }
}