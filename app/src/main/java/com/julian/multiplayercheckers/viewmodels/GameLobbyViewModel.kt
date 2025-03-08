package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.FirebaseDatabase
import com.julian.multiplayercheckers.dataclasses.GameData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GameLobbyViewModel @Inject constructor(
    application: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(application) {

    var gameToken: String = ""

    companion object {
        const val TOKENS = "tokens"
        const val INITIAL_GAME_STATE = """rrrrrrrrrrrrnnnnnnnnbbbbbbbbbbbb"""
        fun generateToken(): String {
            return UUID.randomUUID().toString()
        }
    }

    fun hostGame() {
        gameToken = generateToken()
        val tokensReference = database.getReference(TOKENS)
        tokensReference.child(gameToken).setValue(GameData()).addOnFailureListener {
            Log.e("WTF", "ALE COS SIE ZJEBALO")
        }
    }

    fun cancelGameHosting() {
        database.getReference(TOKENS).child(gameToken).removeValue()
    }
}