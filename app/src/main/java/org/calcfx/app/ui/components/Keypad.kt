package org.calcfx.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.ui.theme.*

data class KeyModel(
    val primaryText: String,
    val keyAction: String = primaryText,
    val shiftText: String? = null,
    val alphaText: String? = null,
    val isNumber: Boolean = false,
    val isOperator: Boolean = false,
    val isEquals: Boolean = false,
    val isActionDel: Boolean = false,
    val isActionAc: Boolean = false
)

@Composable
fun Keypad(
    isShiftActive: Boolean,
    isAlphaActive: Boolean,
    onKeyPress: (String) -> Unit,
    onOpenMoreMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // --- 1. Minimalist Top Control Strip (6 items) ---
        MinimalistControlStrip(
            isShiftActive = isShiftActive,
            isAlphaActive = isAlphaActive,
            onKeyPress = onKeyPress,
            onOpenMoreMenu = onOpenMoreMenu,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.85f)
        )

        // --- 2. Complete Scientific Function Block (4 Rows, 6 Columns) ---
        ScientificFunctionSection(
            onKeyPress = onKeyPress,
            modifier = Modifier
                .fillMaxWidth()
                .weight(4.0f)
        )

        // --- 3. Numeric & Arithmetic Block (4 Rows, 5 Columns with Floating Squircles) ---
        NumericPadSection(
            onKeyPress = onKeyPress,
            modifier = Modifier
                .fillMaxWidth()
                .weight(4.8f)
        )
    }
}

@Composable
private fun MinimalistControlStrip(
    isShiftActive: Boolean,
    isAlphaActive: Boolean,
    onKeyPress: (String) -> Unit,
    onOpenMoreMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAppAccent.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. SHIFT
        MinimalPillButton(
            text = "SHIFT",
            isActive = isShiftActive,
            activeBg = AccentGold,
            activeFg = Color.Black,
            inactiveBg = AccentGold.copy(alpha = 0.12f),
            inactiveFg = AccentGold,
            borderColor = AccentGold.copy(alpha = if (isShiftActive) 0.9f else 0.35f),
            onClick = { onKeyPress("SHIFT") },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        // 2. ALPHA
        MinimalPillButton(
            text = "ALPHA",
            isActive = isAlphaActive,
            activeBg = AccentCoral,
            activeFg = Color.Black,
            inactiveBg = AccentCoral.copy(alpha = 0.12f),
            inactiveFg = AccentCoral,
            borderColor = AccentCoral.copy(alpha = if (isAlphaActive) 0.9f else 0.35f),
            onClick = { onKeyPress("ALPHA") },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        // 3. Step Left Cursor
        MinimalNavButton(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Left",
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            },
            onClick = { onKeyPress("LEFT") },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        // 4. Step Right Cursor
        MinimalNavButton(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Right",
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
            },
            onClick = { onKeyPress("RIGHT") },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        // 5. MODE
        MinimalPillButton(
            text = "MODE",
            isActive = false,
            activeBg = accent,
            activeFg = Color.Black,
            inactiveBg = accent.copy(alpha = 0.12f),
            inactiveFg = accent,
            borderColor = accent.copy(alpha = 0.35f),
            onClick = { onKeyPress("MODE") },
            modifier = Modifier.weight(1f).fillMaxHeight()
        )

        // 6. 3-Dot / More Options Menu Button
        MinimalNavButton(
            icon = {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More Tools",
                    tint = AmoledTextPrimary,
                    modifier = Modifier.size(17.dp)
                )
            },
            onClick = onOpenMoreMenu,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Composable
private fun MinimalPillButton(
    text: String,
    isActive: Boolean,
    activeBg: Color,
    activeFg: Color,
    inactiveBg: Color,
    inactiveFg: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isActive) activeBg else inactiveBg)
            .border(BorderStroke(0.8.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) activeFg else inactiveFg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun MinimalNavButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AmoledFunctionKey)
            .border(BorderStroke(0.8.dp, AmoledCardBorder), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun ScientificFunctionSection(
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Row 1: CALC, ∫dx, x⁻¹, log, ln, CONST
    val row1 = listOf(
        KeyModel("CALC", "CALC", shiftText = "SOLVE", alphaText = "="),
        KeyModel("∫dx", "int_diff", shiftText = "d/dx", alphaText = ":"),
        KeyModel("x⁻¹", "x^-1", shiftText = "x!"),
        KeyModel("log", "log", shiftText = "10ˣ", alphaText = "BIN"),
        KeyModel("ln", "ln", shiftText = "eˣ", alphaText = "e"),
        KeyModel("CONST", "CONST", shiftText = "CONV", alphaText = "HEX")
    )

    // Row 2: a b/c, √, x², ^, (-), ° ' "
    val row2 = listOf(
        KeyModel("a b/c", "frac", shiftText = "d/c", alphaText = "DEC"),
        KeyModel("√", "sqrt", shiftText = "³√"),
        KeyModel("x²", "x^2", shiftText = "x³"),
        KeyModel("^", "^", shiftText = "ˣ√"),
        KeyModel("(-)", "(-)", shiftText = "∠", alphaText = "A"),
        KeyModel("°' \"", "deg", shiftText = "←", alphaText = "B")
    )

    // Row 3: hyp, sin, cos, tan, RCL, ENG
    val row3 = listOf(
        KeyModel("hyp", "hyp", shiftText = "sinh", alphaText = "C"),
        KeyModel("sin", "sin", shiftText = "sin⁻¹", alphaText = "D"),
        KeyModel("cos", "cos", shiftText = "cos⁻¹", alphaText = "E"),
        KeyModel("tan", "tan", shiftText = "tan⁻¹", alphaText = "F"),
        KeyModel("RCL", "RCL", shiftText = "STO"),
        KeyModel("ENG", "ENG", shiftText = "←", alphaText = "i")
    )

    // Row 4: (, ), ,, S<=>D, M+, Abs
    val row4 = listOf(
        KeyModel("(", "(", shiftText = "r", alphaText = "X"),
        KeyModel(")", ")", shiftText = "θ", alphaText = "Y"),
        KeyModel(",", "comma", shiftText = ";", alphaText = "M"),
        KeyModel("S<=>D", "S<=>D", shiftText = "a+bi", alphaText = "r∠θ"),
        KeyModel("M+", "M+", shiftText = "M-", alphaText = "DT"),
        KeyModel("Abs", "abs", shiftText = "Conjg", alphaText = "Arg")
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        KeyRowFlex(row1, onKeyPress, modifier = Modifier.weight(1f), isScientific = true)
        KeyRowFlex(row2, onKeyPress, modifier = Modifier.weight(1f), isScientific = true)
        KeyRowFlex(row3, onKeyPress, modifier = Modifier.weight(1f), isScientific = true)
        KeyRowFlex(row4, onKeyPress, modifier = Modifier.weight(1f), isScientific = true)
    }
}

@Composable
private fun NumericPadSection(
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val row1 = listOf(
        KeyModel("7", "7", shiftText = "CONST", isNumber = true),
        KeyModel("8", "8", shiftText = "CONV", isNumber = true),
        KeyModel("9", "9", shiftText = "CLR", isNumber = true),
        KeyModel("DEL", "DEL", shiftText = "INS", isActionDel = true),
        KeyModel("AC", "AC", shiftText = "OFF", isActionAc = true)
    )
    val row2 = listOf(
        KeyModel("4", "4", shiftText = "[MAT]", isNumber = true),
        KeyModel("5", "5", shiftText = "[VCT]", isNumber = true),
        KeyModel("6", "6", isNumber = true),
        KeyModel("×", "*", shiftText = "nPr", isOperator = true),
        KeyModel("÷", "/", shiftText = "nCr", isOperator = true)
    )
    val row3 = listOf(
        KeyModel("1", "1", shiftText = "[STAT]", isNumber = true),
        KeyModel("2", "2", shiftText = "[DIST]", isNumber = true),
        KeyModel("3", "3", shiftText = "[BASE]", isNumber = true),
        KeyModel("+", "+", shiftText = "Pol", isOperator = true),
        KeyModel("−", "-", shiftText = "Rec", isOperator = true)
    )
    val row4 = listOf(
        KeyModel("0", "0", shiftText = "Rnd", isNumber = true),
        KeyModel(".", ".", shiftText = "Ran#", isNumber = true),
        KeyModel("×10ˣ", "EXP", shiftText = "π", alphaText = "e", isNumber = false),
        KeyModel("Ans", "Ans", shiftText = "%", isNumber = false),
        KeyModel("=", "=", shiftText = "Re<=>Im", isEquals = true)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        KeyRowFlex(row1, onKeyPress, modifier = Modifier.weight(1f), isScientific = false)
        KeyRowFlex(row2, onKeyPress, modifier = Modifier.weight(1f), isScientific = false)
        KeyRowFlex(row3, onKeyPress, modifier = Modifier.weight(1f), isScientific = false)
        KeyRowFlex(row4, onKeyPress, modifier = Modifier.weight(1f), isScientific = false)
    }
}

@Composable
private fun KeyRowFlex(
    keys: List<KeyModel>,
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier,
    isScientific: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (isScientific) 6.dp else 7.dp)
    ) {
        for (k in keys) {
            MinimalCalculatorButton(
                key = k,
                onClick = { onKeyPress(k.keyAction) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                isScientific = isScientific
            )
        }
    }
}

@Composable
private fun MinimalCalculatorButton(
    key: KeyModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isScientific: Boolean = false
) {
    val accent = LocalAppAccent.current

    val bg = when {
        key.isEquals -> accent
        key.isActionAc -> AmoledActionAc
        key.isActionDel -> AmoledActionDel
        key.isOperator -> AmoledOperatorKey
        key.isNumber -> AmoledNumberKey
        else -> AmoledFunctionKey
    }

    val txtColor = when {
        key.isEquals -> Color.Black
        key.isActionAc || key.isActionDel -> AccentCoral
        key.isOperator -> accent
        key.isNumber -> AmoledTextPrimary
        else -> AmoledTextSecondary
    }

    // High-end squircle corner curves: 20dp for numeric keys, 14dp for scientific keys
    val cornerRadius = if (key.isNumber || key.isOperator || key.isEquals || key.isActionAc || key.isActionDel) 20.dp else 14.dp

    Column(
        modifier = modifier
            .semantics { contentDescription = key.primaryText }
            .clip(RoundedCornerShape(cornerRadius))
            .background(bg)
            .border(BorderStroke(0.6.dp, AmoledCardBorder), RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 3.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Shift / Alpha subtle indicator hints
        if (key.shiftText != null || key.alphaText != null) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = key.shiftText ?: "",
                    color = AccentGold.copy(alpha = 0.90f),
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = key.alphaText ?: "",
                    color = AccentCoral.copy(alpha = 0.90f),
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }

        Text(
            text = key.primaryText,
            color = txtColor,
            fontSize = if (key.primaryText.length > 4) 10.sp else if (key.primaryText.length > 2) 11.5.sp else if (key.isNumber || key.isOperator || key.isEquals) 20.sp else 14.sp,
            fontWeight = if (key.isNumber || key.isOperator || key.isEquals) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))
    }
}
