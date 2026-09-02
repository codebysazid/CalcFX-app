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
import org.calcfx.app.engine.Matrix
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixSolverDialog(
    onDismiss: () -> Unit
) {
    // 3x3 Matrix Grid state
    var m00 by remember { mutableStateOf("1") }
    var m01 by remember { mutableStateOf("2") }
    var m02 by remember { mutableStateOf("3") }
    var m10 by remember { mutableStateOf("0") }
    var m11 by remember { mutableStateOf("1") }
    var m12 by remember { mutableStateOf("4") }
    var m20 by remember { mutableStateOf("5") }
    var m21 by remember { mutableStateOf("6") }
    var m22 by remember { mutableStateOf("0") }

    val matrixA = remember(m00, m01, m02, m10, m11, m12, m20, m21, m22) {
        Matrix(
            3,
            3,
            arrayOf(
                doubleArrayOf(m00.toDoubleOrNull() ?: 0.0, m01.toDoubleOrNull() ?: 0.0, m02.toDoubleOrNull() ?: 0.0),
                doubleArrayOf(m10.toDoubleOrNull() ?: 0.0, m11.toDoubleOrNull() ?: 0.0, m12.toDoubleOrNull() ?: 0.0),
                doubleArrayOf(m20.toDoubleOrNull() ?: 0.0, m21.toDoubleOrNull() ?: 0.0, m22.toDoubleOrNull() ?: 0.0)
            )
        )
    }

    var computedDet by remember { mutableStateOf<Double?>(null) }
    var computedMatrix by remember { mutableStateOf<Matrix?>(null) }
    var resultLabel by remember { mutableStateOf("Matrix [A]") }

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
                text = "Matrix Calculator (MATRIX)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Matrix [A] (3x3):", color = AccentCyan, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            // 3x3 Matrix Editor
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MatrixCell(m00, { m00 = it }, Modifier.weight(1f))
                    MatrixCell(m01, { m01 = it }, Modifier.weight(1f))
                    MatrixCell(m02, { m02 = it }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MatrixCell(m10, { m10 = it }, Modifier.weight(1f))
                    MatrixCell(m11, { m11 = it }, Modifier.weight(1f))
                    MatrixCell(m12, { m12 = it }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MatrixCell(m20, { m20 = it }, Modifier.weight(1f))
                    MatrixCell(m21, { m21 = it }, Modifier.weight(1f))
                    MatrixCell(m22, { m22 = it }, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Matrix Operations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        computedDet = matrixA.determinant()
                        computedMatrix = null
                        resultLabel = "Determinant det(A)"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoledFunctionKey),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmoledCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("det(A)", color = AccentGold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        try {
                            computedMatrix = matrixA.inverse()
                            computedDet = null
                            resultLabel = "Inverse A⁻¹"
                        } catch (e: Exception) {
                            resultLabel = "Error: Non-invertible"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoledFunctionKey),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmoledCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("A⁻¹", color = AccentCyan, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        computedMatrix = matrixA.transpose()
                        computedDet = null
                        resultLabel = "Transpose Aᵀ"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoledFunctionKey),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmoledCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aᵀ", color = AccentBlue, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        try {
                            computedMatrix = matrixA * matrixA
                            computedDet = null
                            resultLabel = "Matrix Square A²"
                        } catch (e: Exception) {
                            resultLabel = "Error"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmoledFunctionKey),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmoledCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("A²", color = AccentCoral, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Result Display Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmoledFunctionKey)
                    .border(1.dp, AmoledCardBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(resultLabel, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    if (computedDet != null) {
                        Text(
                            text = "= $computedDet",
                            color = AmoledTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (computedMatrix != null) {
                        val mat = computedMatrix!!
                        for (r in 0 until mat.rows) {
                            val rowStr = mat.data[r].joinToString("    ") {
                                String.format(java.util.Locale.US, "%.4g", it)
                            }
                            Text(
                                text = "[  $rowStr  ]",
                                color = AmoledTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
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
private fun MatrixCell(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier
    )
}
