package jinproject.stepwalk.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

fun AnnotatedString.Builder.appendColorText(text: String, color: Color) {
    withStyle(
        SpanStyle(
            color = color
        )
    ) {
        append(text)
    }
}