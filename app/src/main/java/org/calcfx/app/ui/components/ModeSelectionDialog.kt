package org.calcfx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.engine.AngleUnit
import org.calcfx.app.engine.CalculatorMode
import org.calcfx.app.ui.theme.*

data class ModeItem(val mode: CalculatorMode, val num: Int, val label: String, val desc: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectionDialog(
    currentMode: CalculatorMode,
    currentAngle: AngleUnit,
    onSelectMode: (CalculatorMode) -> Unit,
    onSelectAngle: (AngleUnit) -> Unit,
    onDismiss: () -> Unit
) {
    val modes = listOf(
        ModeItem(CalculatorMode.COMP, 1, "COMP", "Standard Natural Math"),
        ModeItem(CalculatorMode.CMPLX, 2, "CMPLX", "Complex Numbers a+bi"),
        ModeItem(CalculatorMode.STAT, 3, "STAT", "Statistics & Regression"),
        ModeItem(CalculatorMode.BASE_N, 4, "BASE-N", "Programmer Hex/Bin/Dec"),
        ModeItem(CalculatorMode.EQN, 5, "EQN", "Equation Solvers"),
        ModeItem(CalculatorMode.MATRIX, 6, "MATRIX", "Matrix Calculator"),
        ModeItem(CalculatorMode.TABLE, 7, "TABLE", "Function Table f(X)"),
        ModeItem(CalculatorMode.VECTOR, 8, "VECTOR", "Vector Math")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AmoledSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Mode & Setup",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Angle Unit:",
                color = AccentCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AngleChip("1: Deg (°)", currentAngle == AngleUnit.DEGREE, { onSelectAngle(AngleUnit.DEGREE) }, Modifier.weight(1f))
                AngleChip("2: Rad", currentAngle == AngleUnit.RADIAN, { onSelectAngle(AngleUnit.RADIAN) }, Modifier.weight(1f))
                AngleChip("3: Gra", currentAngle == AngleUnit.GRADIAN, { onSelectAngle(AngleUnit.GRADIAN) }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Calculator Engine Mode:",
                color = AccentGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp)
            ) {
                items(modes) { item ->
                    val isSelected = item.mode == currentMode
                    val border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AccentCyan) else androidx.compose.foundation.BorderStroke(1.dp, AmoledCardBorder)
                    val bg = if (isSelected) AccentCyan.copy(alpha = 0.15f) else AmoledFunctionKey

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .border(border, RoundedCornerShape(10.dp))
                            .clickable { onSelectMode(item.mode) }
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "${item.num}: ${item.label}",
                                color = if (isSelected) AccentCyan else AmoledTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = item.desc,
                                color = AmoledTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AngleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) AccentCyan else AmoledFunctionKey
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AccentCyan else AmoledCardBorder),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else AmoledTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
