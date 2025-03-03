package com.julian.multiplayercheckers.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.julian.multiplayercheckers.R

@Composable
fun CheckersCustomButton(
    onClickFun: () -> Unit = {},
    stringID: Int = R.string.error
) {
    Button(
        onClick = onClickFun,
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.forms_color)
        )
    ) {
        Text(text = stringResource(id = stringID), fontSize = 18.sp)
    }
}
