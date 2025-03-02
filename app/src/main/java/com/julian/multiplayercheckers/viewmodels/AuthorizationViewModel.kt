package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
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
                auth.signInWithEmailAndPassword(email, password).await()
                onResult(Result.success(true))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun trySignUp(email: String, password: String, onResult: (Result<Boolean>) -> Unit) {
        coroutineScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                onResult(Result.success(true))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}