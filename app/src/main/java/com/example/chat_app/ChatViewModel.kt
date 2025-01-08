package com.example.chat_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    val timestamp: Timestamp,
    val senderName: String
    )

data class Chat (
    val chatId: String,
    val membersId: List<String>,
    var messages: MutableList<Message>? = null,
      // val messageReadBy: List<String>, // Ska vara bortkommenterad. Sista funktionalitet om vi hinner.
    val lastUpdated: Timestamp,
    val chatName: String
)

class ChatViewModel : ViewModel() {
    private val db = Firebase.firestore

    private var _chats: MutableLiveData<MutableList<Chat>> = MutableLiveData(mutableListOf())
    val chats: MutableLiveData<MutableList<Chat>> = _chats

    private var _chat: MutableLiveData<Chat?> = MutableLiveData(null)
    val chat: LiveData<Chat?> = _chat

    var chatsListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null

    fun attachChatsListener(userId: String, listenerEmail: String) {
        chatsListener?.remove()

        chatsListener = db.collection("chats")
            .whereArrayContains("membersId", userId)
            .orderBy("lastUpdated")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ChatsListener", "Listen failed.", e)
                    return@addSnapshotListener
                }

                snapshot?.documents?.forEach { doc ->
                    Log.d("ChatsListener", "Document: ${doc.id}, Data: ${doc.data}")
                }

                if (snapshot != null) {
                    val chatsList = snapshot.documents.mapNotNull { doc ->
                        var chatName = ""
                        val emails = doc.get("emails") as? List<String> ?: emptyList()
                        Log.d("emails", emails.toString())

                        for (email in emails) {
                            if (email != listenerEmail) {
                                chatName = email
                                break
                            }
                        }

                        try {
                            Chat(
                                chatId = doc.id,
                                membersId = doc.get("memberIds") as? List<String> ?: emptyList(),
                                lastUpdated = doc.getTimestamp("lastUpdated") ?: Timestamp.now(),
                                chatName = chatName
                            )
                        } catch (ex: Exception) {
                            Log.e("ChatsListener", "Error parsing chat document", ex)
                            null
                        }
                    }

                    _chats.postValue(chatsList.toMutableList())
                } else {
                    _chats.postValue(mutableListOf())
                }
            }
    }

    suspend fun createConversation(creatorEmail: String, creatorUserId: String, receiverEmail: String, onUpdate: (result: CreateChatStatus, chatId: String?) -> Unit) {
        try {
            val receiverUser = db.collection("users")
                .whereEqualTo("email", receiverEmail)
                .limit(1)
                .get()
                .await()

            Log.d("!!!!", receiverUser.documents.toString())
            if (receiverUser.isEmpty) {
                onUpdate(CreateChatStatus.USERNOTFOUND, null)
            }

            val receiverUserId = receiverUser.documents[0].id

            val chatExists = db.collection("chats")
                .whereArrayContains("membersId", creatorUserId)
                .get()
                .await()

            val chatWithBothUsers = if (chatExists.isEmpty) null else {
                chatExists.documents.firstOrNull { doc ->
                    val members = doc.get("membersId") as? List<*>
                    members?.contains(receiverUserId) == true
                }
            }

            if (chatWithBothUsers != null) {
                onUpdate(CreateChatStatus.CHATEXISTS, null)
                return
            }

            val data = hashMapOf(
                "membersId" to arrayListOf<String>(creatorUserId, receiverUserId),
                "lastUpdated" to FieldValue.serverTimestamp(),
                "emails" to arrayListOf<String>(creatorEmail, receiverEmail)
            )

            val newChatId = db.collection("chats").add(data).await()
            onUpdate(CreateChatStatus.SUCCESS, newChatId.id)
        } catch (ex: Exception) {
            onUpdate(CreateChatStatus.FAILURE, null)
            Log.d("!!!!", ex.toString())
        }
    }

    suspend fun attachChatListener(chatId: String, listenerEmail: String) {
        chatListener?.remove()

        val chatDoc = db.collection("chats").document(chatId).get().await()

        val membersId = chatDoc.get("membersId") as? List<String> ?: emptyList()
        val emails = chatDoc.get("emails") as? List<String> ?: emptyList()
        var chatName = ""
        for (email in emails) {
            if (email != listenerEmail) {
                chatName = email!!
                break
         }
        }

        _chat.value = Chat(
            chatId = chatDoc.id,
            membersId = membersId,
            lastUpdated = chatDoc.getTimestamp("lastUpdated") ?: Timestamp.now(),
            messages = mutableListOf(),
            chatName = chatName
        )

        val messagesRef = db.collection("chats")
            .document(chatId)
            .collection("messages")

        chatListener = messagesRef
            .orderBy("timestamp")
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatViewModel", "Error listening to messages: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messageList = snapshot.documents.mapNotNull { doc ->
                        try {
                            Message(
                                messageId = doc.id,
                                messageByUserId = doc.getString("messageByUserId") ?: "",
                                message = doc.getString("message") ?: "",
                                timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now(),
                                senderName = doc.getString("senderName") ?: ""
                            )
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "Error parsing message document", e)
                            null
                        }
                    }

                    val mutableMessageList = messageList.toMutableList()

                    _chat.value = _chat.value?.copy(
                        messages = mutableMessageList
                    )
                    Log.d("ChatViewModel", "Loaded messages: $messageList")
                }
            }
    }

    suspend fun createMessage(chatId: String, message: String, userId: String, senderName: String) {
        db.collection("chats").document(chatId).collection("messages")
            .add(
                hashMapOf(
                    "message" to message,
                    "senderName" to senderName,
                    "messageByUserId" to userId,
                    "timestamp" to FieldValue.serverTimestamp(),
                )
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
