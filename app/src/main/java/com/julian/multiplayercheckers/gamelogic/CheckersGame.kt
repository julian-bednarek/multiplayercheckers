package com.julian.multiplayercheckers.gamelogic

import com.julian.multiplayercheckers.dataclasses.GameData.Companion.GUEST_TURN
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_TURN
import com.julian.multiplayercheckers.enums.FieldStates

class CheckersGame {

    companion object {
        val directions: List<Pair<Int, Int>> = listOf(
            Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1)
        )
    }


    private var _playerTurn: Int? = null
    private var whosTurn: Int = HOST_TURN

    val currentTurn: Int
        get() = whosTurn

    private val playerTurn: Int
        get() = _playerTurn ?: throw IllegalStateException("Player turn not initialized")

    fun initializePlayerTurn(turn: Int) {
        if (_playerTurn == null) {
            _playerTurn = turn
        }
    }

    val board: Board = Board()
    private var selectedPiece: Position? = null

    interface BoardStateListener {
        fun onBoardStateChanged(board: Array<Array<FieldStates>>)
    }

    private var boardStateListener: BoardStateListener? = null

    fun setBoardStateListener(listener: BoardStateListener) {
        this.boardStateListener = listener
    }

    private fun notifyBoardStateChanged() {
        boardStateListener?.onBoardStateChanged(board.grid)
    }

    fun loadFromString(stateStr: String, turn: Int) {
        board.loadFromString(stateStr)
        whosTurn = turn
        notifyBoardStateChanged()
    }

    fun onPieceSelected(row: Int, col: Int) {
        if (whosTurn != playerTurn) return
        if (selectedPiece != null) {
            val move = Move(selectedPiece!!, Position(row, col))
            if (isValidMove(move)) {
                makeMove(move)
                selectedPiece = null
            } else {
                val playablePieces = when (playerTurn) {
                    HOST_TURN -> listOf(FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN)
                    else -> listOf(FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN)
                }
                selectedPiece = null
                if (board[Position(row, col)] in playablePieces) {
                    selectedPiece = Position(row, col)
                }
            }
        } else {
            val playablePieces = when (playerTurn) {
                HOST_TURN -> listOf(FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN)
                else -> listOf(FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN)
            }
            selectedPiece = if (board[Position(row, col)] in playablePieces) {
                Position(row, col)
            } else {
                null
            }
        }
    }

    private fun isValidMove(move: Move): Boolean {
        val validMoves: List<Move> = getValidMovesForPiece(move.from)
        return validMoves.any { it.to == move.to }
    }

    private fun isValidPosition(position: Position): Boolean {
        return position.row in 0 until BOARD_SIZE && position.col in 0 until BOARD_SIZE
    }

    private fun isOpponentPiece(myPiece: FieldStates, other: FieldStates): Boolean {
        return when (myPiece) {
            FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN ->
                other == FieldStates.PLAYER_2 || other == FieldStates.PLAYER_2_QUEEN
            FieldStates.PLAYER_2, FieldStates.PLAYER_2_QUEEN ->
                other == FieldStates.PLAYER_1 || other == FieldStates.PLAYER_1_QUEEN
            else -> false
        }
    }

    private fun getValidMovesForPiece(position: Position): List<Move> {
        val piece = board[position]
        val moves = mutableListOf<Move>()

        for (direction in directions) {
            if (direction.first > 0 && piece == FieldStates.PLAYER_2) continue
            if (direction.first < 0 && piece == FieldStates.PLAYER_1) continue
            val adjacent = Position(position.row + direction.first, position.col + direction.second)
            if (!isValidPosition(adjacent)) continue

            if (board[adjacent] == FieldStates.EMPTY) {
                moves.add(Move(position, adjacent))
            } else if (isOpponentPiece(piece, board[adjacent])) {
                val jumpPos = Position(adjacent.row + direction.first, adjacent.col + direction.second)
                if (isValidPosition(jumpPos) && board[jumpPos] == FieldStates.EMPTY) {
                    moves.add(Move(position, jumpPos))
                }
            }
        }

        val capturingMoves = moves.filter { kotlin.math.abs(it.to.row - it.from.row) == 2 }
        return capturingMoves.ifEmpty { moves }
    }

    private fun handlePotentialCapture(move: Move) {
        if (kotlin.math.abs(move.to.row - move.from.row) == 2) {
            val capturedRow = (move.from.row + move.to.row) / 2
            val capturedCol = (move.from.col + move.to.col) / 2
            board[Position(capturedRow, capturedCol)] = FieldStates.EMPTY
        }
    }

    private fun handlePotentialPromotion(move: Move) {
        val piece = board[move.from]
        when (piece) {
            FieldStates.PLAYER_1 -> {
                if (move.to.row == BOARD_SIZE - 1) {
                    board[move.to] = FieldStates.PLAYER_1_QUEEN
                }
            }
            FieldStates.PLAYER_2 -> {
                if (move.to.row == 0) {
                    board[move.to] = FieldStates.PLAYER_2_QUEEN
                }
            }
            else -> {}
        }
    }

    private fun makeMove(move: Move) {
        board[move.to] = board[move.from]
        handlePotentialCapture(move)
        handlePotentialPromotion(move)
        board[move.from] = FieldStates.EMPTY
        whosTurn = if (whosTurn == HOST_TURN) GUEST_TURN else HOST_TURN
        notifyBoardStateChanged()
    }
}