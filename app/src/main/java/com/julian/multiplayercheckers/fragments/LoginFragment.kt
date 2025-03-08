package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.julian.multiplayercheckers.R
import com.julian.multiplayercheckers.composables.CheckersInputField
import com.julian.multiplayercheckers.composables.FormCard
import com.julian.multiplayercheckers.composables.TransparentSignUpButton
import com.julian.multiplayercheckers.viewmodels.AuthorizationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val authViewModel: AuthorizationViewModel by viewModels()
    private val errorMessage: MutableState<String> = mutableStateOf("")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LoginFragmentView(
                    onLoginClick = {email, password ->  onLogInClicked(email, password)},
                    onSignUpClick = {onSingUpClicked()},
                    errorMessage = errorMessage.value,
                    onClearError = { errorMessage.value = "" }
                )
            }
        }
    }

    private fun onLogInClicked(email: String, password: String) {
        authViewModel.trySignIn(email, password) { result ->
            activity?.runOnUiThread {
                if (result.isSuccess) {
                    findNavController().navigate(R.id.action_loginFragment_to_startViewFragment)
                } else {
                    errorMessage.value = getString(R.string.exception_login_failed)
                }
            }
        }
    }

    private fun onSingUpClicked() {
        findNavController().navigate(R.id.action_loginFragment_to_singUpFragment)
    }
}



@Composable
fun LoginFragmentView(
    onLoginClick: (email: String, password: String) -> Unit,
    onSignUpClick: () -> Unit,
    errorMessage: String = "",
    onClearError: () -> Unit
) {
    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_color)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FormCard {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    CheckersInputField(
                        onValueChange = {
                            email = it
                            onClearError()
                        },
                        labelResID = R.string.email_label,
                        placeholderResID = R.string.email_placeholder
                    )

                    CheckersInputField(
                        onValueChange = {
                            password = it
                            onClearError()
                        },
                        labelResID = R.string.password_label,
                        placeholderResID = R.string.password_placeholder,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = { onLoginClick(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.forms_util_color)
                        ),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.login_action),
                            color = Color.Black
                        )
                    }
            }
            TransparentSignUpButton(onSignUpClick = onSignUpClick)
        }
    }
}