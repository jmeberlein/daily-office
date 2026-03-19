package com.bcponline.dailyoffice.model

import androidx.compose.ui.graphics.Color

enum class LiturgicalColor(val background: Color, val onBackground: Color = Color(0xFF1C1B1F)) {
    NONE(Color.Transparent),
    GREEN(Color(0xFFC8E6C9)),
    WHITE(Color(0xFFF5F5F5)),
    BLUE(Color(0xFFBBDEFB)),
    PURPLE(Color(0xFFE1BEE7)),
    RED(Color(0xFFFFCDD2)),
    PINK(Color(0xFFF8BBD0)),
    BLACK(Color(0xFF757575), Color(0xFFF5F5F5))
}
