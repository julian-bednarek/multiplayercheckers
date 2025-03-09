package com.julian.multiplayercheckers.dataclasses

import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.INITIAL_GAME_STATE


data class GameData(
    val gameState: String = INITIAL_GAME_STATE,
    /**
     * Game status meaning:
     * - 0: waiting for 2nd player
     * - 1: game started
     * - 2: game cancelled
     * - 3: other game termination
     */
    val gameStatus: Int = 0
)
