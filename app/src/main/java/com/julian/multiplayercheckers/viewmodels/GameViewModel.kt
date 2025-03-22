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
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.GUEST_TURN
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_TURN
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.fragments.BOARD_SIZE
import com.julian.multiplayercheckers.gamelogic.GameHandler
import com.julian.multiplayercheckers.gamelogic.GameHandler.Companion.isPlayable
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.TOKENS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application,
    private val database: DatabaseReference
) : AndroidViewModel(application) {

    private val moveValidator: GameHandler = GameHandler()
    private var gameToken: String? = null

    private val _board: MutableLiveData<Array<Array<FieldStates>>> =
        MutableLiveData(moveValidator.getBoard())
    val board: LiveData<Array<Array<FieldStates>>> get() = _board

    var userTurn: Int = HOST_TURN

    fun observeBoard(gameToken: String) {
        this.gameToken = gameToken
        val gameStateReference = database.child(TOKENS).child(gameToken)
        val gameStateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameData: GameData = snapshot.getValue(GameData::class.java)!!
                    moveValidator.updateBoard(gameData)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        gameStateReference.addValueEventListener(gameStateListener)
    }

    fun onPieceSelected(row: Int, col: Int) {
        moveValidator.onPieceSelected(row, col)
        updateGameState(moveValidator.getBoard())
    }

    private fun updateGameState(board: Array<Array<FieldStates>>) {
        val newGameState = serializeBoard(board)
        gameToken?.let {
            database.child(TOKENS).child(it).child("gameState").setValue(newGameState)
        }
    }

    private fun switchTurn() {
        val newTurn = if (userTurn == HOST_TURN) GUEST_TURN else HOST_TURN
        gameToken?.let {
            database.child(TOKENS).child(it).child("whosTurn").setValue(newTurn)
        }
    }

    private fun serializeBoard(board: Array<Array<FieldStates>>): String {
        val sb = StringBuilder()
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                if (isPlayable(row, col)) {
                    sb.append(
                        when (board[row][col]) {
                            FieldStates.EMPTY -> 'n'
                            FieldStates.PLAYER_1 -> 'r'
                            FieldStates.PLAYER_1_QUEEN -> 'R'
                            FieldStates.PLAYER_2 -> 'b'
                            FieldStates.PLAYER_2_QUEEN -> 'B'
                            else -> 'n'
                        }
                    )
                }
            }
        }
        return sb.toString()
    }
}