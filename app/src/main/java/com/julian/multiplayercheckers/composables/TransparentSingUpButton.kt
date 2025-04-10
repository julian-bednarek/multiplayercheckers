package com.julian.multiplayercheckers.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.julian.multiplayercheckers.R

@Composable
fun TransparentSignUpButton(
    onSignUpClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clickable(
                onClick = onSignUpClick
            )
    ) {
        Text(
            fontSize = 16.sp,
            text = stringResource(id = R.string.sing_up_prompt),
            color = Color.White,
            textDecoration = TextDecoration.Underline
        )
    }
}
