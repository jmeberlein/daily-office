package com.bcponline.dailyoffice.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownAnnotator
import org.intellij.markdown.MarkdownElementTypes

@Composable
fun MarkdownText(markdown: String, rubrics: Boolean = true) {
    val italicColor = if (rubrics) Color.Red else Color.Unspecified
    Markdown(
        content = markdown,
        colors = markdownColor(),
        typography = markdownTypography(),
        annotator = markdownAnnotator { content, child ->
            if (child.type == MarkdownElementTypes.EMPH) {
                pushStyle(SpanStyle(color = italicColor, fontStyle = FontStyle.Italic))
                append(content.substring(child.startOffset + 1, child.endOffset - 1))
                pop()
                true
            } else false
        }
    )
}
