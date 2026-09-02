package org.calcfx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import org.calcfx.app.engine.MathEvaluator
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableModeDialog(
    evaluator: MathEvaluator,
    onDismiss: () -> Unit
) {
    var fxExpr by remember { mutableStateOf("X^2 - 2*X + 1") }
    var startX by remember { mutableStateOf("-3") }
    var endX by remember { mutableStateOf("3") }
    var stepSize by remember { mutableStateOf("1") }

    data class TableRow(val x: Double, val fx: String)

    val rows = remember(fxExpr, startX, endX, stepSize) {
        val start = startX.toDoubleOrNull() ?: -3.0
        val end = endX.toDoubleOrNull() ?: 3.0
        val step = (stepSize.toDoubleOrNull() ?: 1.0).coerceAtLeast(0.01)

        val list = mutableListOf<TableRow>()
        var curr = start
        var count = 0
        while (curr <= end + 1e-9 && count < 100) {
            val yFormatted = try {
                val y = evaluator.evaluateWithVariable(fxExpr, 'X', curr)
                if (y.isNaN() || y.isInfinite()) "Error" else String.format(java.util.Locale.US, "%.5g", y)
            } catch (_: Exception) {
                "Error"
            }
            list.add(TableRow(curr, yFormatted))
            curr += step
            count++
        }
        list
    }

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
                text = "Function Table Generator (TABLE)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = fxExpr,
                onValueChange = { fxExpr = it },
                label = { Text("f(X) =") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = startX,
                    onValueChange = { startX = it },
                    label = { Text("Start") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endX,
                    onValueChange = { endX = it },
                    label = { Text("End") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = stepSize,
                    onValueChange = { stepSize = it },
                    label = { Text("Step") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(AmoledFunctionKey)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("X", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("f(X)", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            // Table Data List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(rows) { r ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = r.x.toString(),
                            color = AmoledTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                        Text(
                            text = r.fx,
                            color = AccentCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                    HorizontalDivider(color = AmoledCardBorder.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
