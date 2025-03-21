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
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.TOKENS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application,
    private val database: DatabaseReference
) : AndroidViewModel(application) {

    private var gameToken: String? = null

    private val _canMove: MutableLiveData<Boolean> = MutableLiveData(false)
    val canMove: LiveData<Boolean> get() = _canMove

    private val _board = MutableStateFlow(Array(BOARD_SIZE) { row ->
        Array(BOARD_SIZE) { col ->
            if ((row + col) % 2 == 1) FieldStates.EMPTY else FieldStates.NOT_USED
        }
    })
    val board: StateFlow<Array<Array<FieldStates>>> = _board.asStateFlow()

    var userTurn: Int = HOST_TURN

    fun observeBoard(gameToken: String) {
        this.gameToken = gameToken
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
        val rowRange = if (userTurn == GUEST_TURN) 0 until BOARD_SIZE else BOARD_SIZE - 1 downTo 0
        for (row in rowRange) {
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

    private fun getValidMoves(row: Int, col: Int): List<Pair<Int, Int>> {
        val piece = _board.value[row][col]
        if (!isCurrentPlayerPiece(piece)) return emptyList()

        val directions = getPieceDirections(piece)
        return directions.flatMap { (deltaRow, deltaCol) ->
            getMovesInDirection(row, col, deltaRow, deltaCol)
        }
    }

    private fun isCurrentPlayerPiece(piece: FieldStates): Boolean {
        return when {
            userTurn == GameData.HOST_TURN && (piece == FieldStates.PLAYER_1 || piece == FieldStates.PLAYER_1_QUEEN) -> true
            userTurn == GameData.GUEST_TURN && (piece == FieldStates.PLAYER_2 || piece == FieldStates.PLAYER_2_QUEEN) -> true
            else -> false
        }
    }

    private fun getPieceDirections(piece: FieldStates): List<Pair<Int, Int>> {
        return when (piece) {
            FieldStates.PLAYER_1 -> listOf(Pair(1, -1), Pair(1, 1))
            FieldStates.PLAYER_2 -> listOf(Pair(-1, -1), Pair(-1, 1))
            FieldStates.PLAYER_1_QUEEN, FieldStates.PLAYER_2_QUEEN ->
                listOf(Pair(1, -1), Pair(1, 1), Pair(-1, -1), Pair(-1, 1))
            else -> emptyList()
        }
    }

    private fun getMovesInDirection(row: Int, col: Int, dr: Int, dc: Int): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        val newRow = row + dr
        val newCol = col + dc
        if (isWithinBounds(newRow, newCol) && isPlayable(newRow, newCol)) {
            if (_board.value[newRow][newCol] == FieldStates.EMPTY
                || _board.value[newRow][newCol] == FieldStates.HINT) {
                moves.add(Pair(newRow, newCol))
            } else {
                moves.addAll(getCaptureMoves(row, col, dr, dc))
            }
        }
        return moves
    }

    private fun getCaptureMoves(row: Int, col: Int, dr: Int, dc: Int): List<Pair<Int, Int>> {
        val piece = _board.value[row][col]
        val newRow = row + dr
        val newCol = col + dc
        val opponent = when (piece) {
            FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN ->
                listOf(FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN)
            FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN ->
                listOf(FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN)
            else -> emptyList()
        }
        if (opponent.contains(_board.value[newRow][newCol])) {
            val jumpRow = row + 2 * dr
            val jumpCol = col + 2 * dc
            if (isWithinBounds(jumpRow, jumpCol) && _board.value[jumpRow][jumpCol] == FieldStates.EMPTY) {
                return listOf(Pair(jumpRow, jumpCol))
            }
        }
        return emptyList()
    }

    private fun isWithinBounds(row: Int, col: Int) =
        row in 0 until BOARD_SIZE && col in 0 until BOARD_SIZE

    private fun isPlayable(row: Int, col: Int) = (row + col) % 2 == 1

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

    fun onPieceSelected(selectedRow: Int, selectedCol: Int) {
        val validMoves: List<Pair<Int, Int>> = getValidMoves(selectedRow, selectedCol)
        _board.update { currentBoard ->
            val newBoard = Array(currentBoard.size) { row ->
                Array(currentBoard[row].size) { col ->
                    when (currentBoard[row][col]) {
                        FieldStates.HINT -> FieldStates.EMPTY
                        else -> currentBoard[row][col]
                    }
                }
            }
            validMoves.forEach { (moveRow, moveCol) ->
                if (newBoard[moveRow][moveCol] == FieldStates.EMPTY) {
                    newBoard[moveRow][moveCol] = FieldStates.HINT
                }
            }
            newBoard
        }
    }

    fun attemptMove(source: Pair<Int, Int>, target: Pair<Int, Int>) {
        if (!isValidMove(source, target)) return

        val updatedBoard = performMove(source, target)
        _board.value = updatedBoard

        updateGameState(updatedBoard)
        switchTurn()
    }

    private fun isValidMove(source: Pair<Int, Int>, target: Pair<Int, Int>): Boolean {
        val validMoves = getValidMoves(source.first, source.second)
        return validMoves.contains(target)
    }

    private fun performMove(source: Pair<Int, Int>, target: Pair<Int, Int>): Array<Array<FieldStates>> {
        val currentBoard = _board.value.map { it.copyOf() }.toTypedArray()
        val piece = currentBoard[source.first][source.second]
        currentBoard[source.first][source.second] = FieldStates.EMPTY

        val updatedBoard = handleCapture(source, target, currentBoard)
        return handlePromotion(target, piece, updatedBoard).also {
            it[target.first][target.second] = if (piece == FieldStates.PLAYER_1 && target.first == BOARD_SIZE - 1) FieldStates.PLAYER_1_QUEEN else if (piece == FieldStates.PLAYER_2 && target.first == 0) FieldStates.PLAYER_2_QUEEN else piece
        }
    }

    private fun handleCapture(
        source: Pair<Int, Int>,
        target: Pair<Int, Int>,
        board: Array<Array<FieldStates>>
    ): Array<Array<FieldStates>> {
        val dr = target.first - source.first
        val dc = target.second - source.second
        if (kotlin.math.abs(dr) == 2 && kotlin.math.abs(dc) == 2) {
            val jumpedRow = source.first + dr / 2
            val jumpedCol = source.second + dc / 2
            board[jumpedRow][jumpedCol] = FieldStates.EMPTY
        }
        return board
    }

    private fun handlePromotion(
        target: Pair<Int, Int>,
        piece: FieldStates,
        board: Array<Array<FieldStates>>
    ): Array<Array<FieldStates>>{
        if (piece == FieldStates.PLAYER_1 && target.first == BOARD_SIZE - 1) {
            board[target.first][target.second] = FieldStates.PLAYER_1_QUEEN
        } else if (piece == FieldStates.PLAYER_2 && target.first == 0) {
            board[target.first][target.second] = FieldStates.PLAYER_2_QUEEN
        } else {
            board[target.first][target.second] = piece
        }
        return board
    }

    private fun updateGameState(board: Array<Array<FieldStates>>) {
        val newGameState = serializeBoard(board)
        gameToken?.let {
            database.child(TOKENS).child(it).child("gameState").setValue(newGameState)
        }
    }

    private fun switchTurn() {
        val newTurn = if (userTurn == GameData.HOST_TURN) GameData.GUEST_TURN else GameData.HOST_TURN
        gameToken?.let {
            database.child(TOKENS).child(it).child("whosTurn").setValue(newTurn)
        }
    }
}