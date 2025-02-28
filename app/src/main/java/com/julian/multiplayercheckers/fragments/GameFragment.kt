package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.julian.multiplayercheckers.composables.GameField
import com.julian.multiplayercheckers.enums.FieldStates

const val BOARD_SIZE: Int = 8

class GameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GameFragmentLayout()
            }
        }
    }
}

@Composable
fun GameFragmentLayout() {
    Column {
        for (i in 1..BOARD_SIZE) {
            Row {
                for (j in 1..BOARD_SIZE) {
                    if (((i + j) % 2) == 1) {
                        GameField(Color.White, FieldStates.PLAYER_1_QUEEN)
                    } else {
                        GameField(Color.Black, FieldStates.PLAYER_2)
                    }
                }
            }
        }
    }
}