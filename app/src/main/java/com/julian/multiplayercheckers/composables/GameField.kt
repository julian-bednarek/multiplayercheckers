package com.julian.multiplayercheckers.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julian.multiplayercheckers.enums.FieldStates


const val FIELD_SIZE: Int = 50
const val FIELD_CONTENT_SIZE: Int = (FIELD_SIZE * 3) / 4
val PLAYER_1_COLOR: Color = Color.Red
val PLAYER_2_COLOR: Color = Color.Blue

@Composable
fun GameField(
    color: Color = Color.Transparent,
    state: FieldStates = FieldStates.EMPTY
) {
    Box(
        modifier = Modifier
            .height(FIELD_SIZE.dp)
            .width(FIELD_SIZE.dp)
            .background(color)
    ) {
        when(state) {
            FieldStates.EMPTY -> return
            FieldStates.PLAYER_1 -> Pawn(PLAYER_1_COLOR)
            FieldStates.PLAYER_2 -> Pawn(PLAYER_2_COLOR)
            FieldStates.PLAYER_1_QUEEN -> Queen(PLAYER_1_COLOR)
            FieldStates.PLAYER_2_QUEEN -> Queen(PLAYER_2_COLOR)
            FieldStates.HINT -> Hint()
        }
    }
}

@Composable
fun Hint() {
    Box(
        modifier = Modifier
            .size(FIELD_CONTENT_SIZE.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
    )
}

@Preview(showBackground = true)
@Composable
fun Pawn(
    color: Color = Color.Red
) {
    Box(
        modifier = Modifier
            .size(FIELD_CONTENT_SIZE.dp)
            .clip(CircleShape)
            .background(color)
    )
}

private fun buildCrownPath(width: Float, height: Float): Path {
    return Path().apply {
        reset()
        moveTo(0f, 0.25f * height)
        lineTo(width * 0.3f, 0.85f * height)
        lineTo(width * 0.7f, 0.85f * height)
        lineTo(width, 0.25f * height)
        lineTo(0.7f * width, 0.4f * height)
        lineTo(0.5f * width, 0.25f * height)
        lineTo(0.3f * width, 0.4f * height)
        lineTo(0f, 0.25f * height)
        close()
    }
}

private fun DrawScope.drawCrown() {
    val w = size.width
    val h = size.height
    val crownPath = buildCrownPath(w, h)
    drawPath(
        path = crownPath,
        color = Color.Yellow,
        style = Fill
    )
}

@Preview(showBackground = true)
@Composable
fun Queen(
    color: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .size(FIELD_SIZE.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCrown()
        }
    }
}