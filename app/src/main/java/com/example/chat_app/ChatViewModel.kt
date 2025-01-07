package com.example.chat_app

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.play.core.integrity.au
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

data class Message(
    val messageId: String,
    val messageByUserId: String,
    val message: String,
    val messageReadBy: List<String>, // Ska vara bortkommenterad. Sista funktionalitet om vi hinner.
    val timestamp: Timestamp
)

data class Chat (
    val chatId: String,
    val chatName: String,
    val memberIds: List<String>,
    var messages: MutableList<Message>? = null,
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
            .orderBy("lastUpdated")
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

    suspend fun createConversation(creatorUserId: String, receiverEmail: String, onUpdate: (result: CreateChatStatus) -> Unit) {
        try {
            val receiverUser = db.collection("users")
                .whereEqualTo("email", receiverEmail)
                .limit(1)
                .get()
                .await()

            Log.d("!!!!", receiverUser.documents.toString())
            if (receiverUser.isEmpty) {
                onUpdate(CreateChatStatus.USERNOTFOUND)
            }

            val receiverUserId = receiverUser.documents[0].id

            val chatExists = db.collection("chats")
                .whereArrayContains("membersIds",listOf(creatorUserId, receiverUserId))
                .limit(1)
                .get()
                .await()

            Log.d("!!!!", chatExists.documents.toString())
            if (chatExists.isEmpty) {
                onUpdate(CreateChatStatus.CHATEXISTS)
            }

            val data = hashMapOf(
                "membersId" to arrayListOf(creatorUserId, receiverUserId),
                "lastUpdated" to FieldValue.serverTimestamp()
            )

            db.collection("chats").add(data).await()
            onUpdate(CreateChatStatus.SUCCESS)
        } catch (ex: Exception) {
            onUpdate(CreateChatStatus.FAILURE)
            Log.d("!!!!", ex.toString())
        }
    }

    suspend fun createMessage(chatId: String, message: String, userId: String) {
        db.collection("chats").document(chatId).collection("messages")
            .add(
                hashMapOf(
                "message" to message,
                "messageByUserId" to userId,
                "timestamp" to FieldValue.serverTimestamp())
            )
            .await()

        db.collection("chats").document(chatId).update("lastUpdated", FieldValue.serverTimestamp()).await()
    }

    override fun onCleared() {
        super.onCleared()
        chatsListener?.remove()
        chatListener?.remove()
    }
}
