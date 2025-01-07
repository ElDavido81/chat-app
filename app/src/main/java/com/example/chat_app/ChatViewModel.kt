package com.example.chat_app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
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
    var messages: List<Message>? = null,
    val lastUpdated: Timestamp
)

class ChatViewModel : ViewModel() {
    private val db = Firebase.firestore

    private var _chats: MutableLiveData<List<Chat>> = MutableLiveData(emptyList())
    val chats: MutableLiveData<List<Chat>> = _chats

    private var _chat: MutableLiveData<Chat?> = MutableLiveData(null)
    val chat: MutableLiveData<Chat?> = _chat

    var chatsListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null

    fun attachChatsListener(userId: String) {
        chatsListener?.remove()
        chatsListener = db.collection("chats")
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ChatsListener", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val chatsList = mutableListOf<Chat>()
                    for (document in snapshot.documents) {
                        try {
                            val chat = Chat(
                                chatId = document.id,
                                chatName = document.getString("chatName") ?: "",
                                memberIds = document.get("memberIds") as? List<String> ?: emptyList(),
                                lastUpdated = document.getTimestamp("lastUpdated") ?: Timestamp.now()
                            )
                            chatsList.add(chat)
                        } catch (ex: Exception) {
                            Log.e("ChatsListener", "Error parsing chat document", ex)
                        }
                    }
                    _chats.value = chatsList
                } else {
                    _chats.value = emptyList()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        chatsListener?.remove()
        chatListener?.remove()
    }
}