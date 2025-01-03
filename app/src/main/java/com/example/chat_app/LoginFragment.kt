package com.example.chat_app

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

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

                if (etEmail.text.toString().isEmpty() || etPassword.text.toString().isEmpty()) {
                    val toast = Toast.makeText(context, "All fields must be filled.", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    login(etEmail.text.toString(), etPassword.text.toString())
                }

        }

    }

    private fun login(etEmail: String, etPassword: String) {
        authViewModel.login(etEmail, etPassword) { user ->
            if (user == null) {
                val toast = Toast.makeText(context, "Incorrect email or password!", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }
}