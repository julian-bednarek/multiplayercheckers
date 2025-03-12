package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.julian.multiplayercheckers.dataclasses.GameData
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.GAME_ENDED
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.GAME_STARTED
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_WAITING_FOR_PLAYER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GameHostingViewModel @Inject constructor(
    application: Application,
    private val database: FirebaseDatabase
) : AndroidViewModel(application) {

    val gameToken: String = generateToken()
    private val _waitingStatus: MutableStateFlow<Int> = MutableStateFlow(0)
    val waitingStatus: StateFlow<Int> get() = _waitingStatus

    companion object {
        const val TOKENS = "tokens"
        const val INITIAL_GAME_STATE = """rrrrrrrrrrrrnnnnnnnnbbbbbbbbbbbb"""
        fun generateToken(): String {
            return UUID.randomUUID().toString()
        }
        const val WAITING_ONGOING = 0
        const val WAITING_END = 1
        const val WAITING_ERROR = 2
    }

    fun hostGame() {
        val tokensReference = database.getReference(TOKENS)
        tokensReference.child(gameToken).setValue(GameData()).addOnFailureListener { exception ->
            Log.e("hostGame", "Failed to host game: ${exception.message}", exception)
        }
        waitUntilAnotherPlayerJoins()
    }

    private fun waitUntilAnotherPlayerJoins() {
        val gameDataRef = database.getReference(TOKENS).child(gameToken)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val gameData = snapshot.getValue(GameData::class.java)
                    val status = gameData?.gameStatus ?: 0
                    when(status){
                        HOST_WAITING_FOR_PLAYER -> {_waitingStatus.value = WAITING_ONGOING}
                        GAME_STARTED -> {_waitingStatus.value = WAITING_END; gameDataRef.removeEventListener(this)}
                        GAME_ENDED -> { _waitingStatus.value = WAITING_ERROR; gameDataRef.removeEventListener(this)}
                        else -> {_waitingStatus.value = WAITING_ERROR}
                    }
                }else{_waitingStatus.value = WAITING_ERROR}
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("waitUntilAnotherPlayerJoins","Listener cancelled: ${error.message}",error.toException())
                _waitingStatus.value = WAITING_ERROR
            }
        }
        gameDataRef.addValueEventListener(listener)
    }



    fun cancelGameHosting() {
        database.getReference(TOKENS).child(gameToken).removeValue()
    }
}