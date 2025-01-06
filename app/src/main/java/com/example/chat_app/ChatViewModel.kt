package com.example.chat_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Message(
    val messageId: String,
    val messageByUserId: String,
    val message: String,
    val messageReadBy: List<String>,
    val timestamp: Timestamp
)

data class Chat(
    val chatId: String,
    val chatName: String,
    val memberIds: List<String>,
    val messages: List<Message>,
    val lastUpdated: Timestamp
)

class ChatViewModel: ViewModel() {
    private val db = Firebase.firestore

    private var _chats: MutableLiveData<List<Chat>> = MutableLiveData<List<Chat>>(emptyList())
    private var _chat: MutableLiveData<Chat?> = MutableLiveData<Chat?>(null)
}