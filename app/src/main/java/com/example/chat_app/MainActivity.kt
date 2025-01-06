package com.example.chat_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chat_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    val authViewModel: AuthViewModel by viewModels()
    val launchFragment = LaunchFragment()
    val chatLogFragment = ChatLogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel.isAuthenticated.observe(this) { value ->
            val isLoading = value.first
            val isLoggedIn = value.second
            if (!isLoading && isLoggedIn) {
                chatLogFragment()
            } else {
                launchFragment()
            }
        }
    }

    private fun launchFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.mainContainer.id, launchFragment)
            commit()
        }
    }

    private fun chatLogFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.mainContainer.id, chatLogFragment)
            commit()
        }
    }
}