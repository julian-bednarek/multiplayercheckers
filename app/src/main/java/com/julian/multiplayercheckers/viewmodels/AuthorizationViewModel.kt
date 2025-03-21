package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    application: Application,
    private val auth: FirebaseAuth
) : AndroidViewModel(application) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun trySignIn(email: String, password: String, onResult: (Result<Boolean>) -> Unit) {
        coroutineScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val result = authResult.user?.let {
                    Result.success(true)
                } ?: Result.failure(Exception("Invalid credentials"))
                onResult(result)
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun trySignUp(email: String, password: String, username: String, onResult: (Result<Boolean>) -> Unit) {
        coroutineScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user?.updateProfile(profileUpdates)
                    }
                }
                onResult(Result.success(true))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}