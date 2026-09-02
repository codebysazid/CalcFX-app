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
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseNDialog(
    onDismiss: () -> Unit
) {
    var inputValue by remember { mutableStateOf("255") }
    var currentBase by remember { mutableStateOf(10) }

    val longValue = remember(inputValue, currentBase) {
        try {
            when (currentBase) {
                16 -> inputValue.toLong(16)
                10 -> inputValue.toLong(10)
                8 -> inputValue.toLong(8)
                2 -> inputValue.toLong(2)
                else -> 0L
            }
        } catch (_: Exception) {
            0L
        }
    }

    val hexStr = remember(longValue) { java.lang.Long.toHexString(longValue).uppercase() }
    val decStr = remember(longValue) { longValue.toString() }
    val octStr = remember(longValue) { java.lang.Long.toOctalString(longValue) }
    val binStr = remember(longValue) { java.lang.Long.toBinaryString(longValue) }

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
                text = "Base-N & Programmer (BASE-N)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inputValue,
                onValueChange = { input ->
                    val filtered = when (currentBase) {
                        2 -> input.filter { it in "01" }
                        8 -> input.filter { it in "01234567" }
                        10 -> input.filter { it.isDigit() || it == '-' }
                        16 -> input.filter { it.isDigit() || it in "abcdefABCDEF" }.uppercase()
                        else -> input
                    }
                    inputValue = filtered
                },
                label = { Text("Value (Base $currentBase)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Base Switcher Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BaseTabButton("DEC", currentBase == 10, { currentBase = 10; inputValue = decStr }, Modifier.weight(1f))
                BaseTabButton("HEX", currentBase == 16, { currentBase = 16; inputValue = hexStr }, Modifier.weight(1f))
                BaseTabButton("BIN", currentBase == 2, { currentBase = 2; inputValue = binStr }, Modifier.weight(1f))
                BaseTabButton("OCT", currentBase == 8, { currentBase = 8; inputValue = octStr }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Synchronous Base Conversion Display
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BaseDisplayRow("HEX", hexStr, AccentCoral)
                BaseDisplayRow("DEC", decStr, AccentCyan)
                BaseDisplayRow("OCT", octStr, AccentGold)
                BaseDisplayRow("BIN", binStr, AccentBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bitwise Quick Operations
            Text("Bitwise Operations on $decStr:", color = AmoledTextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BitwiseChip("NOT (~)", (longValue.inv()).toString(), onClick = { inputValue = (longValue.inv()).toString(); currentBase = 10 })
                BitwiseChip("SHL (<< 1)", (longValue shl 1).toString(), onClick = { inputValue = (longValue shl 1).toString(); currentBase = 10 })
                BitwiseChip("SHR (>> 1)", (longValue shr 1).toString(), onClick = { inputValue = (longValue shr 1).toString(); currentBase = 10 })
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BaseTabButton(
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

@Composable
private fun BaseDisplayRow(
    baseName: String,
    value: String,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AmoledFunctionKey)
            .border(1.dp, AmoledCardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = baseName,
                color = accentColor,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = value,
                color = AmoledTextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun BitwiseChip(
    label: String,
    preview: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = AmoledFunctionKey,
        border = androidx.compose.foundation.BorderStroke(1.dp, AmoledCardBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(text = label, color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "= $preview", color = AmoledTextSecondary, fontSize = 10.sp)
        }
    }
}
