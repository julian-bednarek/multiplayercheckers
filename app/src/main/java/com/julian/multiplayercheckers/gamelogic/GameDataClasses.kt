package com.julian.multiplayercheckers.gamelogic

import com.julian.multiplayercheckers.enums.FieldStates


const val BOARD_SIZE: Int = 8

data class Position(val row: Int, val col: Int)

data class Move(
    val from: Position,
    val to: Position,
    val captured: Position? = null
)


class Board {
    companion object {
        const val SIZE = 8
    }
    val grid: Array<Array<FieldStates>> = Array(SIZE) { row ->
        Array(SIZE) { col ->
            if ((row + col) % 2 == 0) FieldStates.NOT_USED else FieldStates.EMPTY
        }
    }
    operator fun get(pos: Position): FieldStates = grid[pos.row][pos.col]
    operator fun set(pos: Position, state: FieldStates) {
        grid[pos.row][pos.col] = state
    }
    fun isValidPosition(pos: Position): Boolean = pos.row in 0 until SIZE && pos.col in 0 until SIZE

    fun loadFromString(stateStr: String) {
        if (stateStr.length != 32) return
        var index = 0
        for (r in 0 until SIZE) {
            for (c in 0 until SIZE) {
                if ((r + c) % 2 == 1) {
                    grid[r][c] = charToFieldState(stateStr[index])
                    index++
                }
            }
        }
    }
    private fun charToFieldState(c: Char): FieldStates {
        return when (c) {
            'r' -> FieldStates.PLAYER_1
            'R' -> FieldStates.PLAYER_1_QUEEN
            'b' -> FieldStates.PLAYER_2
            'B' -> FieldStates.PLAYER_2_QUEEN
            'n' -> FieldStates.EMPTY
            else -> FieldStates.EMPTY
        }
    }
}
