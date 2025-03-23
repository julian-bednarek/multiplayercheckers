package com.julian.multiplayercheckers.gamelogic

import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_TURN
import com.julian.multiplayercheckers.enums.FieldStates

class CheckersGame {

    private var _playerTurn: Int? = null
    private var whosTurn: Int? = HOST_TURN

    val playerTurn: Int
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

    internal fun notifyBoardStateChanged() {
        boardStateListener?.onBoardStateChanged(board.grid)
    }

    fun loadFromString(stateStr: String, turn: Int) {
        board.loadFromString(stateStr)
        notifyBoardStateChanged()
        whosTurn = turn
    }

    fun onPieceSelected(row: Int, col: Int) {
        if (whosTurn != playerTurn) return
        if (selectedPiece != null) {
            val move = Move(selectedPiece!!, Position(row, col))
            if (isValidMove(move)) {
                makeMove(move)
                selectedPiece = null
            }
        } else {
            selectedPiece = if (board[Position(row, col)] in listOf(FieldStates.PLAYER_1, FieldStates.PLAYER_1_QUEEN)) {
                Position(row, col)
            } else {
                null
            }
        }
    }

    private fun isValidMove(move: Move): Boolean {
        val (from, to) = move
        if (!board.isValidPosition(from) || !board.isValidPosition(to)) return false
        return board[from] == FieldStates.EMPTY || board[to] != FieldStates.EMPTY
    }

    private fun makeMove(move: Move) {
        board[move.to] = board[move.from]
        board[move.from] = FieldStates.EMPTY
        notifyBoardStateChanged()
    }
}