package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.julian.multiplayercheckers.R
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.julian.multiplayercheckers.composables.StartViewCustomButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartViewFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = auth.currentUser?.displayName!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StartFragmentView(
                    {onHostGameButtonCLicked()},
                    {onJoinGameButtonClicked()},
                    {onSignOutButtonClicked()},
                    username
                )
            }
        }
    }

    private fun onHostGameButtonCLicked() {
        findNavController().navigate(R.id.action_startViewFragment_to_hostGameFragment)
    }

    private fun onJoinGameButtonClicked() {
        findNavController().navigate(R.id.action_startViewFragment_to_joinGameFragment)
    }

    private fun onSignOutButtonClicked() {
        auth.signOut()
        findNavController().navigate(R.id.action_startViewFragment_to_loginFragment)
    }
}

@Composable
fun StartFragmentView(
    hostGameOnClick: () -> Unit = {},
    joinGameOnClick: () -> Unit = {},
    signOutOnClick:  () -> Unit = {},
    username: String = ""
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_color))
    ) {
        IconButton(
            onClick = signOutOnClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.sign_out),
                modifier = Modifier
                    .size(50.dp),
                tint = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${stringResource(id = R.string.hello_msg)} $username",
                color = Color.White,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(100.dp))
            StartViewCustomButton(
                onClickFun = joinGameOnClick,
                stringID = R.string.join_game
            )
            Spacer(modifier = Modifier.height(10.dp))
            StartViewCustomButton(
                onClickFun = hostGameOnClick,
                stringID = R.string.host_game
            )
        }
    }
}
