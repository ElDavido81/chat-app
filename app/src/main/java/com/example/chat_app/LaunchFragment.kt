package com.example.chat_app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class LaunchFragment : Fragment() {
    val loginFragment = LoginFragment()
    val registerFragment = RegisterFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_goto_login).setOnClickListener {
            goToLogin(loginFragment)
        }

        view.findViewById<Button>(R.id.btn_goto_register).setOnClickListener {
            goToRegister(registerFragment)
        }
    }

    private fun goToLogin(loginFragment: LoginFragment) {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.main_container, loginFragment)
                addToBackStack("launchfragment")
                commit()
            }
    }

    private fun goToRegister(registerFragment: RegisterFragment) {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.main_container, registerFragment)
            addToBackStack("launchfragment")
            commit()
        }
    }
}