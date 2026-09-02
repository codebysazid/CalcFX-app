package org.calcfx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.engine.StatisticsEngine
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsDialog(
    onDismiss: () -> Unit
) {
    var rawInputX by remember { mutableStateOf("12, 15, 14, 10, 18, 20, 22, 19, 14, 17") }
    var rawInputY by remember { mutableStateOf("25, 30, 28, 20, 35, 42, 45, 38, 29, 34") }
    var selectedTab by remember { mutableStateOf(0) }

    val xList = remember(rawInputX) {
        rawInputX.split(",", " ", "\n").mapNotNull { it.trim().toDoubleOrNull() }
    }
    val yList = remember(rawInputY) {
        rawInputY.split(",", " ", "\n").mapNotNull { it.trim().toDoubleOrNull() }
    }

    val oneVar = remember(xList) { StatisticsEngine.calculate1Var(xList) }
    val twoVar = remember(xList, yList) { StatisticsEngine.calculate2Var(xList, yList) }

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
                text = "Statistics & Regression (STAT)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AmoledSurface,
                contentColor = AccentCyan
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("1-Variable Stats", color = if (selectedTab == 0) AccentCyan else AmoledTextSecondary) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("2-Var (Linear y=a+bx)", color = if (selectedTab == 1) AccentCyan else AmoledTextSecondary) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rawInputX,
                onValueChange = { rawInputX = it },
                label = { Text("Data Values X (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedTab == 1) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rawInputY,
                    onValueChange = { rawInputY = it },
                    label = { Text("Data Values Y (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Results Display Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmoledFunctionKey)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (selectedTab == 0 && oneVar != null) {
                        Text("1-Variable Summary (n = ${oneVar.n})", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatRow("Mean (x̄)", oneVar.mean)
                        StatRow("Sum (Σx)", oneVar.sumX)
                        StatRow("Sum of Squares (Σx²)", oneVar.sumX2)
                        StatRow("Population StdDev (σx)", oneVar.populationStdDev)
                        StatRow("Sample StdDev (sx)", oneVar.sampleStdDev)
                        StatRow("Min (minX)", oneVar.min)
                        StatRow("Median (Med)", oneVar.median)
                        StatRow("Max (maxX)", oneVar.max)
                    } else if (selectedTab == 1 && twoVar != null) {
                        Text("Linear Regression: y = a + bx", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatRow("Intercept (a)", twoVar.interceptA)
                        StatRow("Slope (b)", twoVar.slopeB)
                        StatRow("Correlation (r)", twoVar.correlationR)
                        StatRow("R²", twoVar.rSquared)
                        StatRow("Mean X (x̄)", twoVar.meanX)
                        StatRow("Mean Y (ȳ)", twoVar.meanY)
                    } else {
                        Text("Enter at least 2 data points to compute statistics.", color = AmoledTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = AmoledTextSecondary, fontSize = 13.sp)
        val formatted = String.format(java.util.Locale.US, "%.5g", value)
        Text(text = formatted, color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}
