package org.calcfx.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.engine.ConversionPair
import org.calcfx.app.engine.UnitConverter
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterDialog(
    onDismiss: () -> Unit
) {
    var inputValue by remember { mutableStateOf("1") }
    var selectedCategory by remember { mutableStateOf("Length") }

    val categories = remember { UnitConverter.ALL.map { it.category }.distinct() }
    val filteredPairs = remember(selectedCategory) {
        UnitConverter.ALL.filter { it.category == selectedCategory }
    }

    val inputNum = inputValue.toDoubleOrNull() ?: 1.0
    val accent = LocalAppAccent.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AmoledSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Metric & Unit Converter (CONV)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Input Value") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category selector tabs
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 0.dp,
                containerColor = AmoledSurface,
                contentColor = accent
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = cat == selectedCategory,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                color = if (cat == selectedCategory) accent else AmoledTextSecondary,
                                fontWeight = if (cat == selectedCategory) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPairs) { pair ->
                    val converted = UnitConverter.convert(inputNum, pair.code)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AmoledFunctionKey),
                        border = BorderStroke(0.6.dp, AmoledCardBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$inputValue ${pair.fromUnit}",
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = AmoledTextPrimary
                            )
                            Text(
                                text = "➔",
                                fontSize = 15.sp,
                                color = accent
                            )
                            val cleanConverted = String.format(java.util.Locale.US, "%.6g", converted)
                            Text(
                                text = "$cleanConverted ${pair.toUnit}",
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = accent
                            )
                        }
                    }
                }
            }
        }
    }
}
