package jinproject.stepwalk.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

fun AnnotatedString.Builder.appendColorText(text: String, color: Color) {
    withStyle(
        SpanStyle(
            color = color
        )
    ) {
        append(text)
    }
}

fun AnnotatedString.Builder.appendFontSizeWithColorText(text: String, color: Color, fontSize: TextUnit) {
    withStyle(
        SpanStyle(
            color = color,
            fontSize = fontSize,
        )
    ) {
        append(text)
    }
}