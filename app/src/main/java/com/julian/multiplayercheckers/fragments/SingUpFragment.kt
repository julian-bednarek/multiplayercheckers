package com.julian.multiplayercheckers.fragments

import android.os.Bundle
import android.util.Log
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
import com.julian.multiplayercheckers.composables.AuthorizationInputField
import com.julian.multiplayercheckers.composables.FormCard
import com.julian.multiplayercheckers.viewmodels.AuthorizationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingUpFragment : Fragment() {

    private val authViewModel: AuthorizationViewModel by viewModels()
    private val errorMessage: MutableState<String> = mutableStateOf("")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SingUpFragmentView(
                    onSignUpClick = {email, password, repeatedPassword, username ->
                        onSignUpClick(email, password, repeatedPassword, username)},
                    errorMessage = errorMessage.value,
                    onClearError = {}
                )
            }
        }
    }

    private fun onSignUpClick(email: String, password: String, repeatedPassword: String, username: String) {
        activity?.runOnUiThread {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {errorMessage.value = getString(R.string.exception_invalid_email); return@runOnUiThread;}
            else if (password != repeatedPassword) {errorMessage.value = getString(R.string.exception_password_dont_match); return@runOnUiThread;}
            else if (password.length < 8) {errorMessage.value = getString(R.string.exception_too_weak_password); return@runOnUiThread;}
            else if (username.isEmpty()) {errorMessage.value = getString(R.string.exception_empty_username); return@runOnUiThread;}
            authViewModel.trySignUp(email, password, username) { result ->
                if(result.isSuccess) {
                    findNavController().navigate(R.id.action_singUpFragment_to_startViewFragment)
                } else {
                    errorMessage.value = getString(R.string.exception_email_already_used)
                }
            }
        }
    }
}

@Composable
fun SingUpFragmentView(onSignUpClick: (email: String, password: String,
                                       repeatedPassword: String, username: String) -> Unit,
                       errorMessage: String = "",
                       onClearError: () -> Unit) {

    var username: String by remember {mutableStateOf("")}
    var singUpEmail: String by remember {mutableStateOf("")}
    var password: String by remember {mutableStateOf("")}
    var repeatedPassword: String by remember { mutableStateOf("")}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_color)),
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
                AuthorizationInputField(
                    onValueChange = {
                        username = it
                        onClearError()
                    },
                    labelResID = R.string.username_label,
                    placeholderResID = R.string.username_placeholder
                )
                AuthorizationInputField(
                    onValueChange = {
                        singUpEmail = it
                        onClearError()
                    },
                    labelResID = R.string.email_label,
                    placeholderResID = R.string.email_placeholder
                )
                AuthorizationInputField(
                    onValueChange = {
                        password = it
                        onClearError()
                    },
                    labelResID = R.string.password_label,
                    placeholderResID = R.string.password_placeholder,
                    visualTransformation = PasswordVisualTransformation()
                )
                AuthorizationInputField(
                    onValueChange = {
                        repeatedPassword = it
                        onClearError()
                    },
                    labelResID = R.string.retype_password_label,
                    placeholderResID = R.string.retype_password_placeholder,
                    visualTransformation = PasswordVisualTransformation()
                )

                Button(
                    onClick = { onSignUpClick(singUpEmail, password, repeatedPassword, username) },
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
        }
    }
}