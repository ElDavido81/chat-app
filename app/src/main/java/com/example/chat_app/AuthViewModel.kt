package com.example.chat_app

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class User(val name: String, val userId: String, val email: String)

class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private var _isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
    private var _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAuthenticated: MediatorLiveData<Pair<Boolean, Boolean>> = MediatorLiveData<Pair<Boolean, Boolean>>().apply {
        addSource(_isLoading) { loading ->
            value = Pair(loading, _isLoggedIn.value ?: false)
        }
        addSource(_isLoggedIn) { loggedIn ->
            value = Pair(_isLoading.value ?: true, loggedIn)
        }
    }
    private var _user: MutableLiveData<User?> = MutableLiveData<User?>(null)
    val user: MutableLiveData<User?> = _user

    init {
        checkIfUserIsLoggedIn {
            _user.value = it
        }
        auth.addAuthStateListener {
            _isLoggedIn.value = it.currentUser != null
            _isLoading.value = it.currentUser == null
            if (it.currentUser == null) {
                _user.value = null
            }
            Log.d("auth2", _isLoggedIn.value.toString())
            Log.d("auth2", _isLoading.value.toString())
        }
    }

    private fun checkIfUserIsLoggedIn(onUpdate: (user: User?) -> Unit) {
        if (auth.currentUser != null) {
            db.collection("users").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    onUpdate(User(it["name"].toString(), auth.currentUser!!.uid, auth.currentUser!!.email.toString()))
                    this._isLoading.value = false
                }
                .addOnFailureListener {
                    onUpdate(null)
                }
        }
    }

    suspend fun login(email: String, password: String, onUpdate: (isUser: FirebaseUser?) -> Unit) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()

            Log.d("currentuser3", authResult.user?.uid ?: "Fail")
            val userData = db.collection("users").document(authResult.user!!.uid).get().await()
            val name = userData.getString("name")
            _user.value = User(name!!, authResult.user!!.uid, email)
            onUpdate(authResult.user)
        } catch (e: Exception) {
            Log.d("currentuser2", auth.currentUser.toString())
            onUpdate(null)
        }
    }

    suspend fun register(name: String, email: String, password: String, onUpdate: (message: AuthStatus) -> Unit) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            val userId = authResult.user?.uid
            if (userId != null) {
                db.collection("users").document(userId)
                    .set(mapOf(
                        "name" to name,
                        "email" to authResult.user?.email
                    )).await()

                _user.value = User(name, userId, email)
                onUpdate(AuthStatus.SUCCESS)
                this._isLoading.value = false
            } else {
                onUpdate(AuthStatus.FAILURE)
            }
        } catch (e: Exception) {
            if (password.length < 6) {
                onUpdate(AuthStatus.PASSWORD_TOO_SHORT)
                return
            } else onUpdate(AuthStatus.NETWORKISSUES)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}