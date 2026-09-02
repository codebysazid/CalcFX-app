package org.calcfx.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import org.calcfx.app.engine.Constants
import org.calcfx.app.engine.ScientificConstant
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConstantsBottomSheet(
    onSelectConstant: (ScientificConstant) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) Constants.ALL
        else Constants.ALL.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.symbol.contains(searchQuery, ignoreCase = true) ||
            it.code.toString() == searchQuery
        }
    }

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
                text = "Scientific Constants (CODATA)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name, symbol or code (e.g. h, c, 28)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { constItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectConstant(constItem) },
                        colors = CardDefaults.cardColors(containerColor = AmoledFunctionKey),
                        border = BorderStroke(0.6.dp, AmoledCardBorder),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Badge(containerColor = LocalAppAccent.current.copy(alpha = 0.2f)) {
                                        Text("#${constItem.code}", color = LocalAppAccent.current)
                                    }
                                    Text(
                                        text = constItem.symbol,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = LocalAppAccent.current
                                    )
                                    Text(
                                        text = constItem.name,
                                        fontSize = 14.sp,
                                        color = AmoledTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "= ${constItem.value} ${constItem.unit}",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = AmoledTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
