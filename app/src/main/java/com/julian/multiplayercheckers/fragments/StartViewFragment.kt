package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.julian.multiplayercheckers.R
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.julian.multiplayercheckers.composables.CheckersCustomButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StartFragmentView({ onHostGameButtonCLicked() }, { onJoinGameButtonClicked() })
            }
        }
    }

    private fun onHostGameButtonCLicked() {
        findNavController().navigate(R.id.action_startViewFragment_to_gameFragment)
    }

    private fun onJoinGameButtonClicked() {

    }
}

@Composable
fun StartFragmentView(
    hostGameOnClick: () -> Unit = {},
    joinGameOnClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CheckersCustomButton(
            hostGameOnClick,
            R.string.join_game
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        CheckersCustomButton(
            joinGameOnClick,
            R.string.host_game
        )
    }
}