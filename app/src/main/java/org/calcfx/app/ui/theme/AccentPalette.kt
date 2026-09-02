package org.calcfx.app.ui.theme

import androidx.compose.ui.graphics.Color

enum class AccentTheme(
    val id: String,
    val label: String,
    val color: Color
) {
    DYNAMIC("dynamic", "Dynamic (System)", Color(0xFF38BDF8)),
    CYAN("cyan", "Electric Cyan", Color(0xFF00E5FF)),
    EMERALD("emerald", "Emerald Green", Color(0xFF10B981)),
    ORANGE("orange", "Sunset Orange", Color(0xFFF97316)),
    VIOLET("violet", "Electric Violet", Color(0xFFA855F7)),
    ROSE("rose", "Rose Pink", Color(0xFFF43F5E)),
    AMBER("amber", "Amber Gold", Color(0xFFF59E0B)),
    BLUE("blue", "Sapphire Blue", Color(0xFF3B82F6))
}
