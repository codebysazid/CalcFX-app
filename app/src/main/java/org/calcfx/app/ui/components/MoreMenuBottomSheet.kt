package org.calcfx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreMenuBottomSheet(
    onOpenMode: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenConstants: () -> Unit,
    onOpenConverter: () -> Unit,
    onOpenHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    val accent = LocalAppAccent.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AmoledSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "CalcFX Tools & Options",
                color = AmoledTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            MenuOptionRow(
                icon = Icons.Default.Tune,
                title = "Calculator Modes & Angle Units",
                subtitle = "COMP, CMPLX, STAT, BASE-N, EQN, MATRIX, VECTOR, TABLE",
                iconTint = AccentPurple,
                onClick = {
                    onDismiss()
                    onOpenMode()
                }
            )

            MenuOptionRow(
                icon = Icons.Default.Palette,
                title = "Theme & Accent Colors",
                subtitle = "Dynamic System (Material You) & Curated AMOLED Accents",
                iconTint = accent,
                onClick = {
                    onDismiss()
                    onOpenTheme()
                }
            )

            MenuOptionRow(
                icon = Icons.Default.Science,
                title = "Scientific Constants",
                subtitle = "Speed of light, Planck, Avogadro, Gravitational, Rydberg",
                iconTint = AccentGold,
                onClick = {
                    onDismiss()
                    onOpenConstants()
                }
            )

            MenuOptionRow(
                icon = Icons.Default.SwapHoriz,
                title = "Unit Converter",
                subtitle = "Length, Area, Volume, Mass, Velocity, Pressure, Energy",
                iconTint = AccentCyan,
                onClick = {
                    onDismiss()
                    onOpenConverter()
                }
            )

            MenuOptionRow(
                icon = Icons.Default.History,
                title = "Calculation History Tape",
                subtitle = "View, restore previous expressions, and manage history",
                iconTint = AmoledTextSecondary,
                onClick = {
                    onDismiss()
                    onOpenHistory()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MenuOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AmoledFunctionKey)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AmoledTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = AmoledTextSecondary,
                fontSize = 11.5.sp,
                maxLines = 1
            )
        }
    }
}
