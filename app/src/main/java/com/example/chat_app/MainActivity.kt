package com.example.chat_app

import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chat_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var fragmentContainer: FrameLayout
    lateinit var binding: ActivityMainBinding

    val launchFragment = LaunchFragment()

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

        launchFragment()
    }

    private fun launchFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.mainContainer.id, launchFragment)
            commit()
        }
    }
}