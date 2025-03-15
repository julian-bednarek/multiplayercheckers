package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.julian.multiplayercheckers.dataclasses.GameData
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.fragments.BOARD_SIZE
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.TOKENS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application,
    private val database: DatabaseReference
) : AndroidViewModel(application) {
    private val _canMove: MutableLiveData<Boolean> = MutableLiveData(false)
    val canMove: LiveData<Boolean> get() = _canMove
    private val _board = MutableStateFlow(Array(BOARD_SIZE) { _ ->
        Array(BOARD_SIZE) { _ ->
            FieldStates.EMPTY
        }
    })
    val board: StateFlow<Array<Array<FieldStates>>> = _board.asStateFlow()

    var userTurn: Int = 0
        set(value) {
            field = value
        }

    fun observeBoard(gameToken: String) {
        val gameStateReference = database.child(TOKENS).child(gameToken)
        val gameStateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameData: GameData = snapshot.getValue(GameData::class.java)!!
                    _canMove.value = gameData.whosTurn == userTurn
                    updateBoard(gameData.gameState)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        gameStateReference.addValueEventListener(gameStateListener)
    }

    private fun updateBoard(gameState: String) {
        val newBoard = Array(BOARD_SIZE) { row ->
            Array(BOARD_SIZE) { col -> board.value[row][col] }
        }
        var index = 0
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                if ((row + col) % 2 == 1) {
                    if (index < gameState.length) {
                        val stateField: Char = gameState[index]
                        newBoard[row][col] = when(stateField) {
                            'r' -> FieldStates.PLAYER_1
                            'R' -> FieldStates.PLAYER_1_QUEEN
                            'n' -> FieldStates.EMPTY
                            'b' -> FieldStates.PLAYER_2
                            'B' -> FieldStates.PLAYER_2_QUEEN
                            else -> FieldStates.EMPTY
                        }
                        index++
                    }
                } else {
                    newBoard[row][col] = FieldStates.NOT_USED
                }
            }
        }
        _board.value = newBoard
    }
}