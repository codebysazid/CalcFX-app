package org.calcfx.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.calcfx.app.data.CalculatorPreferences
import org.calcfx.app.engine.*

data class HistoryItem(val expression: String, val result: String, val timestamp: Long = System.currentTimeMillis())

data class CalculatorUiState(
    val expression: String = "",
    val cursorPosition: Int = 0,
    val resultPreview: String = "",
    val formattedResult: String = "",
    val decimalResult: String = "",   // preserves original decimal for S⇔D toggle
    val exactFraction: String? = null,
    val exactRadical: String? = null,
    val isShiftActive: Boolean = false,
    val isAlphaActive: Boolean = false,
    val isHypActive: Boolean = false,
    val angleUnit: AngleUnit = AngleUnit.DEGREE,
    val mode: CalculatorMode = CalculatorMode.COMP,
    val isShowingExact: Boolean = false,
    val history: List<HistoryItem> = emptyList(),
    val hasMemoryValue: Boolean = false,
    val accentTheme: org.calcfx.app.ui.theme.AccentTheme = org.calcfx.app.ui.theme.AccentTheme.DYNAMIC,
    val isHistoryOpen: Boolean = false,
    val isConstantsOpen: Boolean = false,
    val isConverterOpen: Boolean = false,
    val isModeDialogOpen: Boolean = false,
    val isThemePickerOpen: Boolean = false,
    val isEquationOpen: Boolean = false,
    val isMatrixOpen: Boolean = false,
    val isVectorOpen: Boolean = false,
    val isBaseNOpen: Boolean = false,
    val isTableOpen: Boolean = false,
    val isStatsOpen: Boolean = false
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    val evaluator = MathEvaluator()
    private val prefs = CalculatorPreferences(application)

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var memoryStore: Double = 0.0

    init {
        // Load persistent settings, history & memory
        val savedAngle = prefs.loadAngleUnit()
        val savedHistory = prefs.loadHistory()
        val savedAccentId = prefs.loadAccentTheme()
        val savedAccent = org.calcfx.app.ui.theme.AccentTheme.values().find { it.id == savedAccentId } ?: org.calcfx.app.ui.theme.AccentTheme.DYNAMIC
        memoryStore = prefs.loadMemory()

        evaluator.angleUnit = savedAngle
        _uiState.update {
            it.copy(
                angleUnit = savedAngle,
                history = savedHistory,
                accentTheme = savedAccent,
                hasMemoryValue = memoryStore != 0.0
            )
        }
    }

    fun onKeyPress(label: String) {
        triggerHaptic()

        when (label) {
            "SHIFT" -> {
                _uiState.update { it.copy(isShiftActive = !it.isShiftActive, isAlphaActive = false) }
                return
            }
            "ALPHA" -> {
                _uiState.update { it.copy(isAlphaActive = !it.isAlphaActive, isShiftActive = false) }
                return
            }
            "MODE" -> {
                _uiState.update { it.copy(isModeDialogOpen = true) }
                return
            }
            "ON", "AC" -> {
                _uiState.update {
                    it.copy(
                        expression = "",
                        cursorPosition = 0,
                        resultPreview = "",
                        formattedResult = "",
                        exactFraction = null,
                        exactRadical = null,
                        isShiftActive = false,
                        isAlphaActive = false,
                        isHypActive = false
                    )
                }
                return
            }
            "DEL" -> {
                deleteAtCursor()
                return
            }
            "=" -> {
                calculateFinal()
                return
            }
            "S<=>D" -> {
                toggleExactDecimal()
                return
            }
            "hyp" -> {
                _uiState.update { it.copy(isHypActive = !it.isHypActive) }
                return
            }
            "M+" -> {
                addToMemory()
                return
            }
            "M-" -> {
                subFromMemory()
                return
            }
            "RCL" -> {
                if (_uiState.value.isShiftActive) {
                    // STO: Store current answer into memory using evaluator for accuracy
                    val expr = _uiState.value.expression
                    val ans = if (expr.isNotBlank()) {
                        try { evaluator.evaluate(expr).decimalValue } catch (_: Exception) { 0.0 }
                    } else {
                        evaluator.lastAnswer
                    }
                    memoryStore = ans
                    prefs.saveMemory(ans)
                    _uiState.update { it.copy(hasMemoryValue = ans != 0.0, isShiftActive = false) }
                } else {
                    insertText(memoryStore.toString())
                }
                return
            }
            "HIST" -> {
                _uiState.update { it.copy(isHistoryOpen = true) }
                return
            }
            "CONST" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isConverterOpen = true, isShiftActive = false) }
                } else {
                    _uiState.update { it.copy(isConstantsOpen = true) }
                }
                return
            }
            "CONV" -> {
                _uiState.update { it.copy(isConverterOpen = true) }
                return
            }
            "LEFT" -> {
                moveCursor(-1)
                return
            }
            "RIGHT" -> {
                moveCursor(1)
                return
            }
            "7" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isConstantsOpen = true, isShiftActive = false) }
                    return
                }
            }
            "8" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isConverterOpen = true, isShiftActive = false) }
                    return
                }
            }
            "4" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isMatrixOpen = true, isShiftActive = false) }
                    return
                }
            }
            "5" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isVectorOpen = true, isShiftActive = false) }
                    return
                }
            }
            "1" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isStatsOpen = true, isShiftActive = false) }
                    return
                }
            }
            "3" -> {
                if (_uiState.value.isShiftActive) {
                    _uiState.update { it.copy(isBaseNOpen = true, isShiftActive = false) }
                    return
                }
            }
        }

        // Standard insertion
        val textToInsert = resolveKeyInsertion(label)
        insertText(textToInsert)

        // Reset Shift / Alpha / Hyp after one keypress (authentic Casio behavior)
        if (_uiState.value.isShiftActive || _uiState.value.isAlphaActive || _uiState.value.isHypActive) {
            _uiState.update { it.copy(isShiftActive = false, isAlphaActive = false, isHypActive = false) }
        }
    }

    private fun resolveKeyInsertion(key: String): String {
        val shift = _uiState.value.isShiftActive
        val alpha = _uiState.value.isAlphaActive
        val hyp = _uiState.value.isHypActive

        return when (key) {
            "CALC" -> when {
                alpha -> "="
                shift -> {
                    // Numerical solve equation
                    _uiState.update { it.copy(isEquationOpen = true) }
                    ""
                }
                else -> {
                    calculateFinal()
                    ""
                }
            }
            "int_diff" -> when {
                alpha -> ":"
                shift -> "diff("
                else -> "int("
            }
            "sin" -> when {
                alpha -> "D"
                shift && hyp -> "asinh("
                shift -> "asin("
                hyp -> "sinh("
                else -> "sin("
            }
            "cos" -> when {
                alpha -> "E"
                shift && hyp -> "acosh("
                shift -> "acos("
                hyp -> "cosh("
                else -> "cos("
            }
            "tan" -> when {
                alpha -> "F"
                shift && hyp -> "atanh("
                shift -> "atan("
                hyp -> "tanh("
                else -> "tan("
            }
            "ln" -> when {
                alpha -> "e"
                shift -> "e^("
                else -> "ln("
            }
            "log" -> if (shift) "10^(" else "log("
            "sqrt" -> if (shift) "cbrt(" else "sqrt("
            "x^2" -> if (shift) "^3" else "^2"
            "x^y", "^" -> if (shift) "^(1/" else "^("
            "x^-1" -> if (shift) "!" else "^(-1)"
            "frac" -> "/"
            "(-)" -> if (alpha) "A" else "-"
            "deg" -> if (alpha) "B" else "°"
            "hyp" -> if (alpha) "C" else ""
            "ENG" -> if (alpha) "i" else ""
            "abs" -> if (shift) "abs(" else "abs("
            "comma" -> if (alpha) "M" else ","
            "(" -> if (alpha) "X" else "("
            ")" -> if (alpha) "Y" else ")"
            "nPr" -> if (shift) "nCr" else "nPr"
            "EXP" -> if (shift) "π" else if (alpha) "e" else "*10^"
            "pi" -> "π"
            "Ans" -> if (shift) "%" else "Ans"
            "Pol" -> if (shift) "Rec(" else "Pol("
            "Ran#" -> {
                val randVal = String.format(java.util.Locale.US, "%.3f", Math.random())
                randVal
            }
            "Rnd" -> "abs("
            else -> key
        }
    }

    fun insertText(text: String) {
        _uiState.update { state ->
            val expr = state.expression
            val pos = state.cursorPosition.coerceIn(0, expr.length)
            val newExpr = expr.substring(0, pos) + text + expr.substring(pos)
            val newPos = pos + text.length

            val res = evaluator.evaluate(newExpr)
            state.copy(
                expression = newExpr,
                cursorPosition = newPos,
                resultPreview = if (!res.isError) res.formattedResult else ""
            )
        }
    }

    fun setCursor(position: Int) {
        _uiState.update { state ->
            state.copy(cursorPosition = position.coerceIn(0, state.expression.length))
        }
    }

    private fun deleteAtCursor() {
        _uiState.update { state ->
            val expr = state.expression
            val pos = state.cursorPosition.coerceIn(0, expr.length)
            if (pos > 0) {
                val newExpr = expr.substring(0, pos - 1) + expr.substring(pos)
                val newPos = pos - 1
                val res = evaluator.evaluate(newExpr)
                state.copy(
                    expression = newExpr,
                    cursorPosition = newPos,
                    resultPreview = if (!res.isError) res.formattedResult else ""
                )
            } else {
                state
            }
        }
    }

    private fun moveCursor(delta: Int) {
        _uiState.update { state ->
            val newPos = (state.cursorPosition + delta).coerceIn(0, state.expression.length)
            state.copy(cursorPosition = newPos)
        }
    }

    private fun calculateFinal() {
        val expr = _uiState.value.expression
        if (expr.isBlank()) return

        val res = evaluator.evaluate(expr)
        val formatted = res.formattedResult
        val fractionStr = res.exactFraction?.toFormattedString()
        val radicalStr = res.exactRadical

        val historyItem = HistoryItem(expr, formatted)
        val updatedHistory = (listOf(historyItem) + _uiState.value.history).take(50)
        prefs.saveHistory(updatedHistory)

        _uiState.update {
            it.copy(
                formattedResult = formatted,
                decimalResult = formatted,    // preserve original decimal for S⇔D
                resultPreview = "",
                exactFraction = fractionStr,
                exactRadical = radicalStr,
                isShowingExact = (fractionStr != null && fractionStr != formatted) || radicalStr != null,
                history = updatedHistory
            )
        }
    }

    private fun toggleExactDecimal() {
        _uiState.update { state ->
            val nextState = !state.isShowingExact
            val displayRes = if (nextState) {
                state.exactRadical ?: state.exactFraction ?: state.formattedResult
            } else {
                state.decimalResult   // restore original decimal value
            }
            state.copy(
                isShowingExact = nextState,
                formattedResult = displayRes
            )
        }
    }

    private fun addToMemory() {
        val res = evaluator.evaluate(_uiState.value.expression)
        if (!res.isError) {
            memoryStore += res.decimalValue
            prefs.saveMemory(memoryStore)
            _uiState.update { it.copy(hasMemoryValue = memoryStore != 0.0) }
        }
    }

    private fun subFromMemory() {
        val res = evaluator.evaluate(_uiState.value.expression)
        if (!res.isError) {
            memoryStore -= res.decimalValue
            prefs.saveMemory(memoryStore)
            _uiState.update { it.copy(hasMemoryValue = memoryStore != 0.0) }
        }
    }

    fun setAngleUnit(unit: AngleUnit) {
        evaluator.angleUnit = unit
        prefs.saveAngleUnit(unit)
        _uiState.update { it.copy(angleUnit = unit) }
        reEvaluateCurrent()
    }

    fun setCalculatorMode(mode: CalculatorMode) {
        evaluator.mode = mode
        _uiState.update { state ->
            state.copy(
                mode = mode,
                isModeDialogOpen = false,
                isEquationOpen = mode == CalculatorMode.EQN,
                isMatrixOpen = mode == CalculatorMode.MATRIX,
                isVectorOpen = mode == CalculatorMode.VECTOR,
                isBaseNOpen = mode == CalculatorMode.BASE_N,
                isTableOpen = mode == CalculatorMode.TABLE,
                isStatsOpen = mode == CalculatorMode.STAT
            )
        }
        reEvaluateCurrent()
    }

    private fun reEvaluateCurrent() {
        val expr = _uiState.value.expression
        if (expr.isNotBlank()) {
            val res = evaluator.evaluate(expr)
            _uiState.update { it.copy(resultPreview = if (!res.isError) res.formattedResult else "") }
        }
    }

    fun insertConstant(constant: ScientificConstant) {
        insertText(constant.value.toString())
        _uiState.update { it.copy(isConstantsOpen = false) }
    }

    fun insertHistoryItem(item: HistoryItem) {
        _uiState.update {
            it.copy(
                expression = item.expression,
                cursorPosition = item.expression.length,
                formattedResult = item.result,
                isHistoryOpen = false
            )
        }
    }

    fun setAccentTheme(theme: org.calcfx.app.ui.theme.AccentTheme) {
        prefs.saveAccentTheme(theme.id)
        _uiState.update { it.copy(accentTheme = theme, isThemePickerOpen = false) }
    }

    fun openThemePicker() {
        _uiState.update { it.copy(isThemePickerOpen = true) }
    }

    fun clearHistory() {
        prefs.saveHistory(emptyList())
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun dismissDialogs() {
        _uiState.update {
            it.copy(
                isHistoryOpen = false,
                isConstantsOpen = false,
                isConverterOpen = false,
                isModeDialogOpen = false,
                isThemePickerOpen = false,
                isEquationOpen = false,
                isMatrixOpen = false,
                isVectorOpen = false,
                isBaseNOpen = false,
                isTableOpen = false,
                isStatsOpen = false
            )
        }
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(10)
            }
        } catch (_: Exception) {}
    }
}
