package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.julian.multiplayercheckers.R
import com.julian.multiplayercheckers.composables.CheckersInputField
import com.julian.multiplayercheckers.composables.FormCard
import com.julian.multiplayercheckers.composables.GeneralLayout
import com.julian.multiplayercheckers.composables.LobbyCustomButton
import com.julian.multiplayercheckers.viewmodels.GameJoiningViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinGameFragment : Fragment() {
    private val viewModel: GameJoiningViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val tokenValid by viewModel.tokenValid.collectAsState()
                JoinGameView(
                    viewModel,
                    onVerifyTokenClickFun = {verifyToken()},
                    onCancelClickFun = {cancelJoiningGame()}
                )
                LaunchedEffect(tokenValid) {
                    tokenValid?.let { valid ->
                        if(valid) {
                            val action = JoinGameFragmentDirections.actionJoinGameFragmentToGameFragment(viewModel.tokenInput.value)
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(requireContext(), "Invalid token", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun verifyToken() {
        viewModel.validateToken()
    }

    private fun cancelJoiningGame() {
        findNavController().navigate(R.id.action_joinGameFragment_to_startViewFragment)
    }
}

@Composable
fun JoinGameView(
    viewModel: GameJoiningViewModel,
    onCancelClickFun: () -> Unit = {},
    onVerifyTokenClickFun: () -> Unit = {}
) {
    GeneralLayout {
                CheckersInputField(
                    onValueChange = {viewModel.setToken(it)},
                    labelResID = R.string.enter_token_label,
                    placeholderResID = R.string.enter_token_placeholder
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LobbyCustomButton(
                        onVerifyTokenClickFun,
                        stringID = R.string.join_game,
                        modifier = Modifier.weight(.5f)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    LobbyCustomButton(
                        onClickFun = onCancelClickFun,
                        stringID = R.string.cancel_hosting,
                        modifier = Modifier.weight(.5f)
                    )
                }
            }
        }
    }
}