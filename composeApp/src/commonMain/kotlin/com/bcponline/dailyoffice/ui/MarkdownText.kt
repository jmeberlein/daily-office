package com.bcponline.dailyoffice.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle

/**
 * Renders a subset of Markdown: italicized text (*text* or _text_) is displayed in red.
 */
@Composable
fun MarkdownText(markdown: String) {
    val annotated = buildAnnotatedString {
        val regex = Regex("""(\*([^*]+)\*|_([^_]+)_)""")
        var cursor = 0
        for (match in regex.findAll(markdown)) {
            append(markdown.substring(cursor, match.range.first))
            val italicText = match.groupValues[2].ifEmpty { match.groupValues[3] }
            pushStyle(SpanStyle(color = Color.Red, fontStyle = FontStyle.Italic))
            append(italicText)
            pop()
            cursor = match.range.last + 1
        }
        append(markdown.substring(cursor))
    }
    Text(text = annotated, style = LocalTextStyle.current)
}
