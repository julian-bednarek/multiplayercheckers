package com.julian.multiplayercheckers.composables

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.julian.multiplayercheckers.R

@Composable
fun StartViewCustomButton(
    onClickFun: () -> Unit = {},
    stringID: Int = R.string.error,
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

@Composable
fun CopyTokenButton(token: String) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Button(
        modifier = Modifier
            .width(320.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.forms_util_color)
        ),
        shape = RoundedCornerShape(5.dp),
        onClick = {
            clipboardManager.setText(AnnotatedString(token))
            Toast.makeText(context, "Token copied to clipboard", Toast.LENGTH_SHORT).show()
        },
    ) {
        Text(text = token, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
fun GeneralCustomButton(
    onClickFun: () -> Unit,
    stringID: Int = R.string.error,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClickFun,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.forms_util_color)
        ),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            stringResource(id = stringID),
            color = Color.Black
        )
    }
}

@Composable
fun FormCustomButton(
    onClickFun: () -> Unit,
    stringID: Int = R.string.error
) {
    GeneralCustomButton(
        onClickFun = onClickFun,
        stringID = stringID,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LobbyCustomButton(
    onClickFun: () -> Unit,
    stringID: Int = R.string.error,
    modifier: Modifier = Modifier
) {
    GeneralCustomButton(
        onClickFun = onClickFun,
        stringID = stringID,
        modifier = modifier
    )
}