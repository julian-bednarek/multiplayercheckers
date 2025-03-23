package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.julian.multiplayercheckers.dataclasses.GameData
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_TURN
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.gamelogic.BOARD_SIZE
import com.julian.multiplayercheckers.gamelogic.CheckersGame
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.TOKENS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application,
    private val database: DatabaseReference
) : AndroidViewModel(application) {

    private val _board = MutableLiveData<Array<Array<FieldStates>>>()
    val board: LiveData<Array<Array<FieldStates>>> = _board
    private val game: CheckersGame = CheckersGame()

    init {
        _board.value = game.board.grid
        game.setBoardStateListener(
            object : CheckersGame.BoardStateListener {
                override fun onBoardStateChanged(board: Array<Array<FieldStates>>) {
                    _board.value = board.map { it.copyOf() }.toTypedArray()
                    val serializedBoard = serializeBoard(board)
                    gameToken?.let { token ->
                        database.child(TOKENS).child(token).child("gameState").setValue(serializedBoard)
                    }
                }
            }
        )
    }

    private var gameToken: String? = null

    private val _canMove: MutableLiveData<Boolean> = MutableLiveData(false)
    val canMove: LiveData<Boolean> get() = _canMove

    var userTurn: Int = HOST_TURN

    fun observeBoard(gameToken: String) {
        this.gameToken = gameToken
        this.game.initializePlayerTurn(userTurn)
        val gameStateReference = database.child(TOKENS).child(gameToken)
        val gameStateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gameData: GameData = snapshot.getValue(GameData::class.java)!!
                    game.loadFromString(gameData.gameState, gameData.whosTurn)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GameViewModel", "Error observing board: ${error.message}")
            }
        }
        gameStateReference.addValueEventListener(gameStateListener)
    }

    private fun isPlayable(row: Int, col: Int) = (row + col) % 2 == 1

    fun onPieceSelected(row: Int, col: Int) {
        if (!isPlayable(row, col)) return
        game.onPieceSelected(row, col)
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