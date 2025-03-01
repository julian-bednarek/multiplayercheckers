package com.julian.multiplayercheckers.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.fragments.BOARD_SIZE
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    val board: Array<IntArray> = Array(BOARD_SIZE) { IntArray(BOARD_SIZE) }

    init {
        for (i in 0 until BOARD_SIZE) {
            for (j in 0 until BOARD_SIZE) {
                board[i][j] = when {
                    i in 0..2 && (i + j) % 2 == 1 -> FieldStates.PLAYER_1.value
                    i in 5..7 && (i + j) % 2 == 1 -> FieldStates.PLAYER_2.value
                    (i + j) % 2 == 1 -> FieldStates.EMPTY.value
                    else -> FieldStates.NOT_USED.value
                }
            }
        }
    }
}