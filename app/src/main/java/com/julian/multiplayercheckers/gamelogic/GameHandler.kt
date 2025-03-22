package com.julian.multiplayercheckers.gamelogic

import com.julian.multiplayercheckers.dataclasses.GameData
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.GUEST_TURN
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_TURN
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.fragments.BOARD_SIZE
import javax.inject.Singleton

@Singleton
class GameHandler(
    private val userTurn: Int = HOST_TURN
) {
    companion object {
        fun isPlayable(row: Int, col: Int) = (row + col) % 2 == 1
        fun isWithinBounds(row: Int, col: Int) =
            row in 0 until BOARD_SIZE && col in 0 until BOARD_SIZE
    }

    private var board: Array<Array<FieldStates>> = Array(BOARD_SIZE) { row ->
        Array(BOARD_SIZE) { col ->
            if (isPlayable(row, col)) FieldStates.EMPTY else FieldStates.NOT_USED
        }
    }

    fun getBoard(): Array<Array<FieldStates>> {
        return Array(BOARD_SIZE) { row -> board[row].copyOf() }
    }

    fun updateBoard(gameData: GameData) {
        val newBoard = Array(BOARD_SIZE) { row ->
            Array(BOARD_SIZE) { col -> board[row][col] }
        }
        var index = 0
        val rowRange = if (gameData.whosTurn == GUEST_TURN) 0 until BOARD_SIZE else BOARD_SIZE - 1 downTo 0
        for (row in rowRange) {
            for (col in 0 until BOARD_SIZE) {
                if (isPlayable(row, col)) {
                    if (index < gameData.gameState.length) {
                        val stateField = gameData.gameState[index]
                        newBoard[row][col] = when (stateField) {
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
        board = newBoard
    }

    fun getValidMoves(row: Int, col: Int): List<Pair<Int, Int>> {
        val piece = board[row][col]
        if (!isCurrentPlayerPiece(piece)) return emptyList()
        val directions = getPieceDirections(piece)
        return directions.flatMap { (dr, dc) -> getMovesInDirection(row, col, dr, dc) }
    }

    private fun isCurrentPlayerPiece(piece: FieldStates): Boolean {
        return when {
            userTurn == HOST_TURN && (piece == FieldStates.PLAYER_1 || piece == FieldStates.PLAYER_1_QUEEN) -> true
            userTurn == GUEST_TURN && (piece == FieldStates.PLAYER_2 || piece == FieldStates.PLAYER_2_QUEEN) -> true
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
            if (board[newRow][newCol] == FieldStates.EMPTY ||
                board[newRow][newCol] == FieldStates.HINT) {
                moves.add(Pair(newRow, newCol))
            } else {
                moves.addAll(getCaptureMoves(row, col, dr, dc))
            }
        }
        return moves
    }

    private fun getCaptureMoves(row: Int, col: Int, dr: Int, dc: Int): List<Pair<Int, Int>> {
        val piece = board[row][col]
        val newRow = row + dr
        val newCol = col + dc
        val opponent = when (piece) {
            FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN ->
                listOf(FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN)
            FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN ->
                listOf(FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN)
            else -> emptyList()
        }
        if (opponent.contains(board[newRow][newCol])) {
            val jumpRow = row + 2 * dr
            val jumpCol = col + 2 * dc
            if (isWithinBounds(jumpRow, jumpCol) && board[jumpRow][jumpCol] == FieldStates.EMPTY) {
                return listOf(Pair(jumpRow, jumpCol))
            }
        }
        return emptyList()
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
            board[0][target.second] = FieldStates.PLAYER_2_QUEEN
        } else {
            board[target.first][target.second] = piece
        }
        return board
    }
}
