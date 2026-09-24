package com.example.eventmanagement

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.auth.ui.activity.Authentication
import com.example.eventmanagement.databinding.ActivityMainBinding
import com.example.eventmanagement.events.ui.Dashboard
import com.example.eventmanagement.utils.NotificationHelper
import com.example.eventmanagement.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        NotificationHelper.createNotificationChannel(this)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)

            val currentUser = FirebaseAuth
                .getInstance()
                .currentUser

            if (currentUser != null) {

                // User is already logged in
                startActivity(
                    Intent(
                        this@SplashActivity,
                        Dashboard::class.java
                    )
                )


            } else {

                // User is not logged in
                startActivity(
                    Intent(
                        this@SplashActivity,
                        Authentication::class.java
                    )
                )
            }

            finish()

        }


    }


}