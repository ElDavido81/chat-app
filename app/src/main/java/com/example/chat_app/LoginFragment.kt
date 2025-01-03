package com.example.chat_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    private val authViewModel = AuthViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            view.findViewById<Button>(R.id.btn_auth_login).setOnClickListener {
            etEmail = view.findViewById(R.id.et_login_email)
            etPassword = view.findViewById(R.id.et_login_password)
            login(etEmail.text.toString().trim(), etPassword.text.toString().trim())
        }

    }

    fun login(etEmail: String, etPassword: String){
        val result = authViewModel.Login(etEmail, etPassword)

        if (result){
            Log.d("!!!", "Succesfully signed in!")
        } else {
            Log.d("!!!", "Not signed in!")
        }

    }
}