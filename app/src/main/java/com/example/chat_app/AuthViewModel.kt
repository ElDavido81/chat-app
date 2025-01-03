package com.example.chat_app

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthViewModel {

    val auth: FirebaseAuth = Firebase.auth



    fun Login(etEmail : String, etPassword : String) : Boolean {

        var status : Boolean = false

        auth.signInWithEmailAndPassword(etEmail, etPassword).addOnCompleteListener { task ->
            if (task.isSuccessful){
                status = true
            } else {
                status = false

            }
        }
        return status
    }

    fun register(name: String, email: String, password: String) {

    }

}