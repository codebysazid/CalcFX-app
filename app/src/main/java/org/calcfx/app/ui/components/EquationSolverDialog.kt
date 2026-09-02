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
import org.calcfx.app.engine.EquationSolution
import org.calcfx.app.engine.EquationSolver
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquationSolverDialog(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Quadratic (ax²+bx+c=0)", "Simultaneous 2x2", "Cubic (ax³+...)", "Simultaneous 3x3")

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
                text = "Equation Solver (EQN)",
                color = AmoledTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = AmoledSurface,
                contentColor = AccentCyan
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) AccentCyan else AmoledTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> QuadraticSolverView()
                1 -> Linear2x2SolverView()
                2 -> CubicSolverView()
                3 -> Linear3x3SolverView()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuadraticSolverView() {
    var a by remember { mutableStateOf("1") }
    var b by remember { mutableStateOf("-5") }
    var c by remember { mutableStateOf("6") }

    val solution = remember(a, b, c) {
        val aVal = a.toDoubleOrNull() ?: 1.0
        val bVal = b.toDoubleOrNull() ?: 0.0
        val cVal = c.toDoubleOrNull() ?: 0.0
        EquationSolver.solveQuadratic(aVal, bVal, cVal)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoeffField(value = a, onValueChange = { a = it }, label = "a (x²)", modifier = Modifier.weight(1f))
            CoeffField(value = b, onValueChange = { b = it }, label = "b (x)", modifier = Modifier.weight(1f))
            CoeffField(value = c, onValueChange = { c = it }, label = "c", modifier = Modifier.weight(1f))
        }

        SolutionCard {
            when (solution) {
                is EquationSolution.Quadratic -> {
                    Text("Roots:", color = AccentGold, fontWeight = FontWeight.Bold)
                    Text("x₁ = ${solution.x1}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                    Text("x₂ = ${solution.x2}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    val vertexType = if (solution.isMinimum) "Min Value (Vertex)" else "Max Value (Vertex)"
                    Text("$vertexType: (${solution.vertexX}, ${solution.vertexY})", color = AccentCyan, fontSize = 13.sp)
                }
                is EquationSolution.Error -> {
                    Text(solution.message, color = AccentCoral)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun Linear2x2SolverView() {
    var a1 by remember { mutableStateOf("2") }
    var b1 by remember { mutableStateOf("3") }
    var c1 by remember { mutableStateOf("8") }
    var a2 by remember { mutableStateOf("4") }
    var b2 by remember { mutableStateOf("-1") }
    var c2 by remember { mutableStateOf("2") }

    val solution = remember(a1, b1, c1, a2, b2, c2) {
        EquationSolver.solveLinear2x2(
            a1.toDoubleOrNull() ?: 1.0,
            b1.toDoubleOrNull() ?: 0.0,
            c1.toDoubleOrNull() ?: 0.0,
            a2.toDoubleOrNull() ?: 0.0,
            b2.toDoubleOrNull() ?: 1.0,
            c2.toDoubleOrNull() ?: 0.0
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Equation 1: a₁x + b₁y = c₁", color = AmoledTextSecondary, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoeffField(value = a1, onValueChange = { a1 = it }, label = "a₁", modifier = Modifier.weight(1f))
            CoeffField(value = b1, onValueChange = { b1 = it }, label = "b₁", modifier = Modifier.weight(1f))
            CoeffField(value = c1, onValueChange = { c1 = it }, label = "c₁", modifier = Modifier.weight(1f))
        }

        Text("Equation 2: a₂x + b₂y = c₂", color = AmoledTextSecondary, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CoeffField(value = a2, onValueChange = { a2 = it }, label = "a₂", modifier = Modifier.weight(1f))
            CoeffField(value = b2, onValueChange = { b2 = it }, label = "b₂", modifier = Modifier.weight(1f))
            CoeffField(value = c2, onValueChange = { c2 = it }, label = "c₂", modifier = Modifier.weight(1f))
        }

        SolutionCard {
            when (solution) {
                is EquationSolution.Linear2D -> {
                    Text("Solution:", color = AccentGold, fontWeight = FontWeight.Bold)
                    Text("x = ${solution.x}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                    Text("y = ${solution.y}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
                }
                is EquationSolution.Error -> {
                    Text(solution.message, color = AccentCoral)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun CubicSolverView() {
    var a by remember { mutableStateOf("1") }
    var b by remember { mutableStateOf("-6") }
    var c by remember { mutableStateOf("11") }
    var d by remember { mutableStateOf("-6") }

    val solution = remember(a, b, c, d) {
        EquationSolver.solveCubic(
            a.toDoubleOrNull() ?: 1.0,
            b.toDoubleOrNull() ?: 0.0,
            c.toDoubleOrNull() ?: 0.0,
            d.toDoubleOrNull() ?: 0.0
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CoeffField(value = a, onValueChange = { a = it }, label = "a (x³)", modifier = Modifier.weight(1f))
            CoeffField(value = b, onValueChange = { b = it }, label = "b (x²)", modifier = Modifier.weight(1f))
            CoeffField(value = c, onValueChange = { c = it }, label = "c (x)", modifier = Modifier.weight(1f))
            CoeffField(value = d, onValueChange = { d = it }, label = "d", modifier = Modifier.weight(1f))
        }

        SolutionCard {
            when (solution) {
                is EquationSolution.Cubic -> {
                    Text("Roots:", color = AccentGold, fontWeight = FontWeight.Bold)
                    Text("x₁ = ${solution.x1}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    Text("x₂ = ${solution.x2}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    Text("x₃ = ${solution.x3}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                }
                is EquationSolution.Error -> {
                    Text(solution.message, color = AccentCoral)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun Linear3x3SolverView() {
    var a1 by remember { mutableStateOf("1") }
    var b1 by remember { mutableStateOf("1") }
    var c1 by remember { mutableStateOf("1") }
    var d1 by remember { mutableStateOf("6") }

    var a2 by remember { mutableStateOf("0") }
    var b2 by remember { mutableStateOf("2") }
    var c2 by remember { mutableStateOf("5") }
    var d2 by remember { mutableStateOf("-4") }

    var a3 by remember { mutableStateOf("2") }
    var b3 by remember { mutableStateOf("5") }
    var c3 by remember { mutableStateOf("-1") }
    var d3 by remember { mutableStateOf("27") }

    val solution = remember(a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3) {
        EquationSolver.solveLinear3x3(
            a1.toDoubleOrNull() ?: 0.0, b1.toDoubleOrNull() ?: 0.0, c1.toDoubleOrNull() ?: 0.0, d1.toDoubleOrNull() ?: 0.0,
            a2.toDoubleOrNull() ?: 0.0, b2.toDoubleOrNull() ?: 0.0, c2.toDoubleOrNull() ?: 0.0, d2.toDoubleOrNull() ?: 0.0,
            a3.toDoubleOrNull() ?: 0.0, b3.toDoubleOrNull() ?: 0.0, c3.toDoubleOrNull() ?: 0.0, d3.toDoubleOrNull() ?: 0.0
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Row 1: a₁x + b₁y + c₁z = d₁", color = AmoledTextSecondary, fontSize = 11.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CoeffField(value = a1, onValueChange = { a1 = it }, label = "a₁", modifier = Modifier.weight(1f))
            CoeffField(value = b1, onValueChange = { b1 = it }, label = "b₁", modifier = Modifier.weight(1f))
            CoeffField(value = c1, onValueChange = { c1 = it }, label = "c₁", modifier = Modifier.weight(1f))
            CoeffField(value = d1, onValueChange = { d1 = it }, label = "d₁", modifier = Modifier.weight(1f))
        }

        Text("Row 2: a₂x + b₂y + c₂z = d₂", color = AmoledTextSecondary, fontSize = 11.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CoeffField(value = a2, onValueChange = { a2 = it }, label = "a₂", modifier = Modifier.weight(1f))
            CoeffField(value = b2, onValueChange = { b2 = it }, label = "b₂", modifier = Modifier.weight(1f))
            CoeffField(value = c2, onValueChange = { c2 = it }, label = "c₂", modifier = Modifier.weight(1f))
            CoeffField(value = d2, onValueChange = { d2 = it }, label = "d₂", modifier = Modifier.weight(1f))
        }

        Text("Row 3: a₃x + b₃y + c₃z = d₃", color = AmoledTextSecondary, fontSize = 11.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            CoeffField(value = a3, onValueChange = { a3 = it }, label = "a₃", modifier = Modifier.weight(1f))
            CoeffField(value = b3, onValueChange = { b3 = it }, label = "b₃", modifier = Modifier.weight(1f))
            CoeffField(value = c3, onValueChange = { c3 = it }, label = "c₃", modifier = Modifier.weight(1f))
            CoeffField(value = d3, onValueChange = { d3 = it }, label = "d₃", modifier = Modifier.weight(1f))
        }

        SolutionCard {
            when (solution) {
                is EquationSolution.Linear3D -> {
                    Text("Solution:", color = AccentGold, fontWeight = FontWeight.Bold)
                    Text("x = ${solution.x}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    Text("y = ${solution.y}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    Text("z = ${solution.z}", color = AmoledTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                }
                is EquationSolution.Error -> {
                    Text(solution.message, color = AccentCoral)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun CoeffField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun SolutionCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AmoledFunctionKey)
            .border(1.dp, AmoledCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            content()
        }
    }
}
