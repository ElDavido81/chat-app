package com.example.chat_app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthViewModel: ViewModel() {
    private var _isLoggedIn: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isLoggedIn: MutableLiveData<Boolean> = _isLoggedIn
    private val auth: FirebaseAuth = Firebase.auth

    init {
        auth.addAuthStateListener {
            _isLoggedIn.value = it.currentUser != null
        }
    }


    fun login(etEmail : String, etPassword : String) {
        auth.signInWithEmailAndPassword(etEmail, etPassword)
    }

    fun register(name: String, email: String, password: String) {

    }
}