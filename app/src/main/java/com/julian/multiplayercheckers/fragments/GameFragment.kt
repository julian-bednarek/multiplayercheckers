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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import com.julian.multiplayercheckers.R
import com.julian.multiplayercheckers.composables.GameField
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.viewmodels.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow

const val BOARD_SIZE: Int = 8
val NOT_USED_COLOR: Color = Color.White
val USED_COLOR: Color = Color.Black

@AndroidEntryPoint
class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                requireArguments().getInt("USER_TURN").let { viewModel.userTurn = it }
                requireArguments().getString("GAME_TOKEN")?.let { GameFragmentLayout(it, viewModel) }
            }
        }
    }
}

@Composable
fun GameFragmentLayout(gameToken: String, viewModel: GameViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_color)),
        contentAlignment = Alignment.Center
    ) {
        viewModel.observeBoard(gameToken)
        Column(
            modifier = Modifier
                .offset(y = (-100).dp)
        ) {
            for (i in 0 until BOARD_SIZE) {
                Row {
                    for (j in 0 until BOARD_SIZE) {
                        ProcessBoardField(viewModel.board, i, j)
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessBoardField(board: StateFlow<Array<Array<FieldStates>>>, row: Int, col: Int) {
    val boardState by board.collectAsState()
    when(boardState[row][col]) {
        FieldStates.NOT_USED  -> GameField(NOT_USED_COLOR, FieldStates.NOT_USED)
        FieldStates.EMPTY     -> GameField(USED_COLOR, FieldStates.EMPTY)
        FieldStates.PLAYER_1  -> GameField(USED_COLOR, FieldStates.PLAYER_1)
        FieldStates.PLAYER_2  -> GameField(USED_COLOR, FieldStates.PLAYER_2)
        FieldStates.PLAYER_1_QUEEN -> GameField(USED_COLOR, FieldStates.PLAYER_1_QUEEN)
        FieldStates.PLAYER_2_QUEEN -> GameField(USED_COLOR, FieldStates.PLAYER_2_QUEEN)
        // TODO CHANGE IT TO PROPER HINT
        FieldStates.HINT -> GameField(NOT_USED_COLOR, FieldStates.NOT_USED)
    }
}