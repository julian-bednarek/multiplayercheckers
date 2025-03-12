package com.julian.multiplayercheckers.dataclasses

import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.INITIAL_GAME_STATE


data class GameData(
    val gameState: String = INITIAL_GAME_STATE,
    val gameStatus: Int = HOST_WAITING_FOR_PLAYER,
    val whosTurn: Int = HOST_TURN
) {
    companion object {
        const val HOST_WAITING_FOR_PLAYER: Int = 0
        const val GAME_STARTED: Int = 1
        const val GAME_ENDED:   Int = 2
        const val HOST_TURN:    Int = 0
        const val GUEST_TURN:   Int = 1
    }
}
