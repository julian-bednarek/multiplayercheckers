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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.julian.multiplayercheckers.dataclasses.GameData.Companion.HOST_TURN
import com.julian.multiplayercheckers.enums.FieldStates
import com.julian.multiplayercheckers.gamelogic.BOARD_SIZE
import com.julian.multiplayercheckers.viewmodels.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

val NOT_USED_COLOR: Color = Color.White
val USED_COLOR: Color = Color.Black
val HINT_COLOR: Color = Color.LightGray

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
            val loopRange = if (viewModel.userTurn == HOST_TURN) 0 until BOARD_SIZE else BOARD_SIZE - 1 downTo 0
            for (i in loopRange) {
                Row {
                    for (j in 0 until BOARD_SIZE) {
                        ProcessBoardField(viewModel, i, j)
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessBoardField(
    viewModel: GameViewModel, row: Int, col: Int,
) {
    val boardState by viewModel.board.observeAsState()
    when(boardState!![row][col]) {
        FieldStates.NOT_USED  -> GameField(NOT_USED_COLOR, FieldStates.NOT_USED
        ) { viewModel.onPieceSelected(row, col) }
        FieldStates.EMPTY     -> GameField(USED_COLOR, FieldStates.EMPTY
        ) { viewModel.onPieceSelected(row, col) }
        FieldStates.PLAYER_1  -> GameField(USED_COLOR, FieldStates.PLAYER_1
        ) { viewModel.onPieceSelected(row, col) }
        FieldStates.PLAYER_2  -> GameField(USED_COLOR, FieldStates.PLAYER_2
        ) { viewModel.onPieceSelected(row, col) }
        FieldStates.PLAYER_1_QUEEN -> GameField(USED_COLOR, FieldStates.PLAYER_1_QUEEN
        ) { viewModel.onPieceSelected(row, col) }
        FieldStates.PLAYER_2_QUEEN -> GameField(USED_COLOR, FieldStates.PLAYER_2_QUEEN
        ) { viewModel.onPieceSelected(row, col) }
        FieldStates.HINT -> GameField(HINT_COLOR, FieldStates.HINT
        ) { viewModel.onPieceSelected(row, col) }
    }
}