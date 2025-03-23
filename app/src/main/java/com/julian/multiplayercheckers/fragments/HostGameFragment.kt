package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.julian.multiplayercheckers.R
import com.julian.multiplayercheckers.composables.CopyTokenButton
import com.julian.multiplayercheckers.composables.FormCard
import com.julian.multiplayercheckers.composables.GeneralCustomButton
import com.julian.multiplayercheckers.composables.GeneralLayout
import com.julian.multiplayercheckers.dataclasses.GameData
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.WAITING_END
import com.julian.multiplayercheckers.viewmodels.GameHostingViewModel.Companion.WAITING_ERROR
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.Duration

@AndroidEntryPoint
class HostGameFragment : Fragment() {

    companion object {
        private var _isHosting = false
    }
    private val viewModel: GameHostingViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                val waitingStatus = viewModel.waitingStatus.collectAsState()
                HostGameView(
                    token = viewModel.gameToken,
                    onCancel = { cancelHosting() }
                )
                LaunchedEffect(waitingStatus.value) {
                    when (waitingStatus.value) {
                        WAITING_END -> {
                            val action = HostGameFragmentDirections.actionHostGameFragmentToGameFragment(viewModel.gameToken, GameData.HOST_TURN)
                            findNavController().navigate(action)
                        }
                        WAITING_ERROR -> {
                            Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_hostGameFragment_to_startViewFragment)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!_isHosting) {
            _isHosting = true
            viewModel.hostGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (requireActivity().isFinishing) {
            viewModel.cancelGameHosting()
            _isHosting = false
        }
    }

    private fun cancelHosting() {
        viewModel.cancelGameHosting()
        _isHosting = false
        findNavController().navigate(R.id.action_hostGameFragment_to_startViewFragment)
    }
}

@Composable
fun HostGameView(
    token: String,
    onCancel: () -> Unit = {}
) {
    GeneralLayout {
        FormCard {
            Text(
                text = "${stringResource(id = R.string.host_token_msg)}\n",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Justify
            )
            CopyTokenButton(token)
            GeneralCustomButton(
                onClickFun = onCancel,
                stringID = R.string.cancel_hosting
            )
        }
    }
}