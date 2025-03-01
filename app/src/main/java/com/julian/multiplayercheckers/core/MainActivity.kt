package com.julian.multiplayercheckers.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.julian.multiplayercheckers.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
        } else {
        }
    }

}