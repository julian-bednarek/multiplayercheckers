package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import com.julian.multiplayercheckers.composables.GameField
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.viewmodels.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

const val BOARD_SIZE: Int = 8

@AndroidEntryPoint
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
fun GameFragmentLayout(viewModel: GameViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF57210D)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset(y = (-100).dp)
        ) {
            for (i in 0..<BOARD_SIZE) {
                Row {
                    for (j in 0..<BOARD_SIZE) {
                        ProcessBoardField(viewModel.board[i][j])
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessBoardField(field: Int) {
    val NOT_USED_COLOR: Color = Color.White
    val USED_COLOR: Color = Color.Black
    when(field) {
        FieldStates.NOT_USED.value  -> GameField(NOT_USED_COLOR, FieldStates.NOT_USED)
        FieldStates.EMPTY.value     -> GameField(USED_COLOR, FieldStates.EMPTY)
        FieldStates.PLAYER_1.value  -> GameField(USED_COLOR, FieldStates.PLAYER_1)
        FieldStates.PLAYER_2.value  -> GameField(USED_COLOR, FieldStates.PLAYER_2)
        FieldStates.PLAYER_1_QUEEN.value -> GameField(USED_COLOR, FieldStates.PLAYER_1_QUEEN)
        FieldStates.PLAYER_2_QUEEN.value -> GameField(USED_COLOR, FieldStates.PLAYER_2_QUEEN)
    }
}