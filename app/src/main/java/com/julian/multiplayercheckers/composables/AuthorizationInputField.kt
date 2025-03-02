package com.julian.multiplayercheckers.composables

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthorizationInputField(
   visualTransformation: VisualTransformation = VisualTransformation.None,
   labelResID: Int,
   placeholderResID: Int,
   onValueChange: (str: String) -> Unit = {}
) {
    var str: String  by remember{mutableStateOf("")}
    val textFieldColorDefaults = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        errorTextColor = Color.Red,
        unfocusedLabelColor = Color.Black,
        focusedTextColor = Color.Black,
        cursorColor = Color.Black,
        disabledLabelColor = Color.Black,
        focusedLabelColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    TextField(
        value = str,
        onValueChange = {
            str = it
            onValueChange(it)
        },
        label = { Text(stringResource(id = labelResID)) },
        placeholder = { Text(stringResource(id = placeholderResID)) },
        textStyle = TextStyle(fontSize = 16.sp),
        visualTransformation = visualTransformation,
        colors = textFieldColorDefaults,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp),
        shape = RoundedCornerShape(5.dp)
    )
}