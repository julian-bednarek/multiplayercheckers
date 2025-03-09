package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.TOKENS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameJoiningViewModel @Inject constructor(
    application: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(application) {

    private val _tokenInput = MutableStateFlow("")
    private val tokenInput: StateFlow<String> = _tokenInput.asStateFlow()

    fun setToken(value: String) {
        _tokenInput.value = value
    }

    private val _tokenValid = MutableStateFlow<Boolean?>(null)
    val tokenValid: StateFlow<Boolean?> = _tokenValid.asStateFlow()

    fun validateToken() {
        val tokensReference = database.getReference(TOKENS)
        tokensReference.child(tokenInput.value).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _tokenValid.value = snapshot.exists()
            }

            override fun onCancelled(error: DatabaseError) {
                _tokenValid.value = false
            }
        })
    }
}