package com.example.chat_app

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.play.core.integrity.au
import com.google.android.play.integrity.internal.c
import com.google.android.play.integrity.internal.u
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class User(val name: String, val userId: String)

class AuthViewModel: ViewModel() {
    private var _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isLoggedIn: MutableLiveData<Boolean> = _isLoggedIn
    private var _user: MutableLiveData<User?> = MutableLiveData<User?>(null)
    val user: MutableLiveData<User?> = _user
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    init {
        checkIfUserIsLoggedIn {
            _user.value = it
        }
        auth.addAuthStateListener {
            _isLoggedIn.value = it.currentUser != null
        }
    }

    private fun checkIfUserIsLoggedIn(onUpdate: (user: User?) -> Unit) {
        if (auth.currentUser != null) {
            db.collection("users").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    onUpdate(User(it["name"].toString(), auth.currentUser!!.uid))
                }
                .addOnFailureListener {
                    onUpdate(null)
                }
        }
    }

    suspend fun login(etEmail: String, etPassword: String, onUpdate: (isUser: FirebaseUser?) -> Unit) {
        try {
            auth.signInWithEmailAndPassword(etEmail, etPassword).await()

            val userData = db.collection("users").document(auth.currentUser!!.uid).get().await()
            _user.value = User(userData["name"].toString(), auth.currentUser!!.uid)
            onUpdate(auth.currentUser)
        } catch (e: Exception) {
            onUpdate(null)
        }
    }

    suspend fun register(name: String, email: String, password: String, onUpdate: (message: AuthStatus) -> Unit) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            val userId = authResult.user?.uid
            if (userId != null) {
                db.collection("users").document(userId)
                    .set(mapOf("name" to name)).await()

                _user.value = User(name, userId)
                onUpdate(AuthStatus.SUCCESS)
            } else {
                onUpdate(AuthStatus.FAILURE)
            }
        } catch (e: Exception) {
            onUpdate(AuthStatus.NETWORKISSUES)
        }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}