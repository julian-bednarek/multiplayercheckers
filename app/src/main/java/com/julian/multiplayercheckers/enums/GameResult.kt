package com.julian.multiplayercheckers.enums

enum class GameResult(value: Int) {
    LOST_BY_FORFEIT(-1),
    LOST(0),
    WON(1),
    WON_BY_FORFEIT(2)
}