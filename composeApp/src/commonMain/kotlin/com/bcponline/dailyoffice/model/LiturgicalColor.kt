package com.bcponline.dailyoffice.model

import androidx.compose.ui.graphics.Color

enum class LiturgicalColor(
    val background: Color,
    val onBackground: Color = Color(0xFF1C1B1F),
    val primary: Color = Color(0xFF1C1B1F)
) {
    NONE(Color.Transparent),
    GREEN(Color(0xFFC8E6C9),  primary = Color(0xFF2E7D32)),
    WHITE(Color(0xFFF5F5F5),  primary = Color(0xFF424242)),
    BLUE(Color(0xFFBBDEFB),   primary = Color(0xFF1565C0)),
    PURPLE(Color(0xFFE1BEE7), primary = Color(0xFF6A1B9A)),
    RED(Color(0xFFFFCDD2),    primary = Color(0xFFC62828)),
    PINK(Color(0xFFF8BBD0),   primary = Color(0xFFAD1457)),
    BLACK(Color(0xFF757575),  onBackground = Color(0xFFF5F5F5), primary = Color(0xFFF5F5F5))
}
