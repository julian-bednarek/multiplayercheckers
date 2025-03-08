package com.julian.multiplayercheckers.dataclasses

import com.julian.multiplayercheckers.enums.GameResult

data class GameHistory(
    val opponent: String,
    val result: GameResult,
    val timestamp: String
)
