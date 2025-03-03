package com.julian.multiplayercheckers.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
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
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
            navHostFragment?.navController?.navigate(
                R.id.startViewFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
            )
        }
    }
}
