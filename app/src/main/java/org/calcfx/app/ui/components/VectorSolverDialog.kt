package org.calcfx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VectorSolverDialog(
    onDismiss: () -> Unit
) {
    var vAx by remember { mutableStateOf("3") }
    var vAy by remember { mutableStateOf("4") }
    var vAz by remember { mutableStateOf("0") }

    var vBx by remember { mutableStateOf("1") }
    var vBy by remember { mutableStateOf("2") }
    var vBz by remember { mutableStateOf("2") }

    val ax = vAx.toDoubleOrNull() ?: 0.0
    val ay = vAy.toDoubleOrNull() ?: 0.0
    val az = vAz.toDoubleOrNull() ?: 0.0

    val bx = vBx.toDoubleOrNull() ?: 0.0
    val by = vBy.toDoubleOrNull() ?: 0.0
    val bz = vBz.toDoubleOrNull() ?: 0.0

    val magA = sqrt(ax * ax + ay * ay + az * az)
    val magB = sqrt(bx * bx + by * by + bz * bz)

    val dotProduct = ax * bx + ay * by + az * bz
    val crossX = ay * bz - az * by
    val crossY = az * bx - ax * bz
    val crossZ = ax * by - ay * bx
    val crossMag = sqrt(crossX * crossX + crossY * crossY + crossZ * crossZ)

    val angleDeg = if (magA > 0 && magB > 0) {
        val cosTheta = (dotProduct / (magA * magB)).coerceIn(-1.0, 1.0)
        Math.toDegrees(acos(cosTheta))
    } else 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AmoledSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Vector Calculator (VECTOR)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Vector [A] (x, y, z):", color = AccentGold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VectorField(vAx, { vAx = it }, "X", Modifier.weight(1f))
                VectorField(vAy, { vAy = it }, "Y", Modifier.weight(1f))
                VectorField(vAz, { vAz = it }, "Z", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Vector [B] (x, y, z):", color = AccentCyan, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VectorField(vBx, { vBx = it }, "X", Modifier.weight(1f))
                VectorField(vBy, { vBy = it }, "Y", Modifier.weight(1f))
                VectorField(vBz, { vBz = it }, "Z", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vector Results
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmoledFunctionKey)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Vector Results:", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    VectorResultRow("Dot Product (A · B)", String.format(java.util.Locale.US, "%.5g", dotProduct))
                    VectorResultRow("Cross Product (A × B)", "[ $crossX, $crossY, $crossZ ]")
                    VectorResultRow("Angle Between (θ)", String.format(java.util.Locale.US, "%.4g°", angleDeg))
                    VectorResultRow("Magnitude |A|", String.format(java.util.Locale.US, "%.5g", magA))
                    VectorResultRow("Magnitude |B|", String.format(java.util.Locale.US, "%.5g", magB))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VectorField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun VectorResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = AmoledTextSecondary, fontSize = 13.sp)
        Text(text = value, color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
