package org.calcfx.app.engine

import kotlin.math.*

enum class AngleUnit {
    DEGREE, RADIAN, GRADIAN
}

enum class CalculatorMode {
    COMP, CMPLX, STAT, BASE_N, EQN, MATRIX, VECTOR, TABLE
}

data class CalculationResult(
    val decimalValue: Double,
    val formattedResult: String,
    val exactFraction: Fraction? = null,
    val exactRadical: String? = null,
    val complexResult: ComplexNumber? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class MathEvaluator(
    var angleUnit: AngleUnit = AngleUnit.DEGREE,
    var mode: CalculatorMode = CalculatorMode.COMP
) {
    private val variables = mutableMapOf<Char, Double>()
    var lastAnswer: Double = 0.0

    fun setVariable(name: Char, value: Double) {
        variables[name.uppercaseChar()] = value
    }

    fun getVariable(name: Char): Double {
        return variables[name.uppercaseChar()] ?: 0.0
    }

    fun evaluate(expression: String): CalculationResult {
        if (expression.isBlank()) return CalculationResult(0.0, "0", Fraction.ZERO)

        // 1. Auto-balance unclosed parentheses
        val balancedExpr = autoBalanceParentheses(expression)

        try {
            val tokens = ExpressionTokenizer(balancedExpr).tokenize()
            if (tokens.isEmpty()) return CalculationResult(0.0, "0", Fraction.ZERO)

            val parser = Parser(tokens, angleUnit, lastAnswer, variables, this)
            val value = parser.parseExpression()

            if (value.isNaN()) {
                return CalculationResult(0.0, "Math ERROR", isError = true, errorMessage = "Math ERROR")
            }
            if (value.isInfinite()) {
                return CalculationResult(0.0, "Math ERROR", isError = true, errorMessage = "Math ERROR")
            }

            lastAnswer = value

            val fraction = Fraction.fromDouble(value)
            val radical = detectExactRadical(value, expression)
            val formatted = formatOutput(value)

            return CalculationResult(
                decimalValue = value,
                formattedResult = formatted,
                exactFraction = fraction,
                exactRadical = radical
            )
        } catch (e: Exception) {
            return CalculationResult(
                decimalValue = 0.0,
                formattedResult = "Syntax ERROR",
                isError = true,
                errorMessage = e.message ?: "Syntax ERROR"
            )
        }
    }

    /**
     * Helper to evaluate sub-expression for Calculus engine (derivative/integral)
     */
    fun evaluateWithVariable(expr: String, varName: Char = 'X', varValue: Double): Double {
        val oldVar = variables[varName]
        variables[varName] = varValue
        return try {
            evaluate(expr).decimalValue
        } finally {
            if (oldVar != null) variables[varName] = oldVar else variables.remove(varName)
        }
    }

    private fun autoBalanceParentheses(expr: String): String {
        var openCount = 0
        var closeCount = 0
        for (c in expr) {
            if (c == '(') openCount++
            if (c == ')') closeCount++
        }
        val needed = openCount - closeCount
        return if (needed > 0) {
            expr + ")".repeat(needed)
        } else {
            expr
        }
    }

    private fun detectExactRadical(value: Double, expr: String): String? {
        val absVal = abs(value)
        val sign = if (value < 0) "-" else ""

        // Common special values
        if (abs(absVal - sqrt(2.0)) < 1e-10) return "${sign}√2"
        if (abs(absVal - sqrt(3.0)) < 1e-10) return "${sign}√3"
        if (abs(absVal - sqrt(5.0)) < 1e-10) return "${sign}√5"
        if (abs(absVal - (sqrt(2.0) / 2.0)) < 1e-10) return "${sign}√2/2"
        if (abs(absVal - (sqrt(3.0) / 2.0)) < 1e-10) return "${sign}√3/2"
        if (abs(absVal - (sqrt(3.0) / 3.0)) < 1e-10) return "${sign}√3/3"
        if (abs(absVal - Math.PI) < 1e-10) return "${sign}π"
        if (abs(absVal - (Math.PI / 2.0)) < 1e-10) return "${sign}π/2"
        if (abs(absVal - (Math.PI / 4.0)) < 1e-10) return "${sign}π/4"

        // Check if value squared is an exact fraction or integer
        val squared = value * value
        val intSquared = round(squared)
        if (abs(squared - intSquared) < 1e-8 && intSquared > 0 && intSquared <= 1000) {
            val root = sqrt(intSquared)
            if (abs(root - round(root)) > 1e-8) {
                // Simplify root of integer: e.g. sqrt(12) = 2√3
                val simplified = simplifySquareRoot(intSquared.toInt())
                return "$sign$simplified"
            }
        }
        return null
    }

    private fun simplifySquareRoot(n: Int): String {
        var outside = 1
        var inside = n
        var d = 2
        while (d * d <= inside) {
            while (inside % (d * d) == 0) {
                outside *= d
                inside /= (d * d)
            }
            d++
        }
        return when {
            inside == 1 -> outside.toString()
            outside == 1 -> "√$inside"
            else -> "$outside√$inside"
        }
    }

    private fun formatOutput(value: Double): String {
        val cleanVal = if (abs(value) < 1e-13) 0.0 else value
        if (cleanVal == cleanVal.toLong().toDouble()) {
            return cleanVal.toLong().toString()
        }
        val s = String.format(java.util.Locale.US, "%.10g", cleanVal)
        return s.replace(Regex("\\.?0+(e|$)"), "$1")
    }

    private class Parser(
        private val tokens: List<Token>,
        private val angleUnit: AngleUnit,
        private val ans: Double,
        private val vars: Map<Char, Double>,
        private val evaluator: MathEvaluator
    ) {
        private var idx = 0

        fun parseExpression(): Double = parseAddSub()

        private fun parseAddSub(): Double {
            var left = parseMulDiv()
            while (idx < tokens.size) {
                val tok = tokens[idx]
                if (tok is Token.Operator && (tok.op == '+' || tok.op == '-')) {
                    idx++
                    val right = parseMulDiv()
                    left = if (tok.op == '+') left + right else left - right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseMulDiv(): Double {
            var left = parseUnary()
            while (idx < tokens.size) {
                val tok = tokens[idx]
                if (tok is Token.Operator && (tok.op == '*' || tok.op == '/' || tok.op == 'P' || tok.op == 'C')) {
                    idx++
                    val right = parseUnary()
                    left = when (tok.op) {
                        '*' -> left * right
                        '/' -> {
                            if (right == 0.0) throw ArithmeticException("Division by zero")
                            left / right
                        }
                        'P' -> permutation(left.toInt(), right.toInt())
                        'C' -> combination(left.toInt(), right.toInt())
                        else -> left
                    }
                } else if (tok is Token.Number || tok is Token.Variable || tok is Token.OpenParen || tok is Token.Function) {
                    // Implicit multiplication like 2sin(30) or 5(3+2)
                    val right = parseUnary()
                    left *= right
                } else {
                    break
                }
            }
            return left
        }

        private fun parseUnary(): Double {
            if (idx >= tokens.size) return 0.0
            val tok = tokens[idx]
            if (tok is Token.Operator && tok.op == '-') {
                idx++
                return -parseUnary()
            }
            if (tok is Token.Operator && tok.op == '+') {
                idx++
                return parseUnary()
            }
            return parsePower()
        }

        private fun parsePower(): Double {
            var left = parsePostfix()
            if (idx < tokens.size) {
                val tok = tokens[idx]
                if (tok is Token.Operator && tok.op == '^') {
                    idx++
                    val right = parseUnary() // right-associative, allows -2^2 = -(2^2)
                    left = left.pow(right)
                }
            }
            return left
        }

        private fun parsePostfix(): Double {
            var v = parsePrimary()
            while (idx < tokens.size) {
                when (tokens[idx]) {
                    is Token.Factorial -> {
                        idx++
                        v = factorial(v.toInt())
                    }
                    is Token.Percent -> {
                        idx++
                        v = v / 100.0
                    }
                    else -> break
                }
            }
            return v
        }

        private fun parsePrimary(): Double {
            if (idx >= tokens.size) return 0.0
            val tok = tokens[idx++]
            return when (tok) {
                is Token.Number -> tok.value
                is Token.Ans -> ans
                is Token.Variable -> vars[tok.name.uppercaseChar()] ?: 0.0
                is Token.OpenParen -> {
                    val v = parseExpression()
                    if (idx < tokens.size && tokens[idx] is Token.CloseParen) {
                        idx++
                    }
                    v
                }
                is Token.Function -> {
                    val fnName = tok.name
                    if (idx < tokens.size && tokens[idx] is Token.OpenParen) {
                        idx++
                    }
                    val arg = parseExpression()
                    if (idx < tokens.size && tokens[idx] is Token.CloseParen) {
                        idx++
                    }
                    evalFunction(fnName, arg)
                }
                else -> 0.0
            }
        }

        private fun evalFunction(fn: String, x: Double): Double {
            return when (fn.lowercase()) {
                "sin" -> sin(toRad(x))
                "cos" -> cos(toRad(x))
                "tan" -> tan(toRad(x))
                "asin" -> fromRad(asin(x))
                "acos" -> fromRad(acos(x))
                "atan" -> fromRad(atan(x))
                "sinh" -> sinh(x)
                "cosh" -> cosh(x)
                "tanh" -> tanh(x)
                "asinh" -> ln(x + sqrt(x * x + 1.0))
                "acosh" -> ln(x + sqrt(x * x - 1.0))
                "atanh" -> 0.5 * ln((1.0 + x) / (1.0 - x))
                "ln" -> ln(x)
                "log" -> log10(x)
                "sqrt" -> sqrt(x)
                "cbrt" -> cbrt(x)
                "abs" -> abs(x)
                else -> throw IllegalArgumentException("Unknown function: $fn")
            }
        }

        private fun toRad(angle: Double): Double = when (angleUnit) {
            AngleUnit.DEGREE -> Math.toRadians(angle)
            AngleUnit.RADIAN -> angle
            AngleUnit.GRADIAN -> angle * (Math.PI / 200.0)
        }

        private fun fromRad(rad: Double): Double = when (angleUnit) {
            AngleUnit.DEGREE -> Math.toDegrees(rad)
            AngleUnit.RADIAN -> rad
            AngleUnit.GRADIAN -> rad * (200.0 / Math.PI)
        }

        private fun factorial(n: Int): Double {
            if (n < 0 || n > 170) throw ArithmeticException("Factorial out of range")
            var res = 1.0
            for (i in 2..n) res *= i
            return res
        }

        private fun permutation(n: Int, r: Int): Double {
            if (r < 0 || r > n) return 0.0
            var res = 1.0
            for (i in (n - r + 1)..n) res *= i
            return res
        }

        private fun combination(n: Int, r: Int): Double {
            if (r < 0 || r > n) return 0.0
            val k = minOf(r, n - r)
            var res = 1.0
            for (i in 1..k) {
                res = res * (n - k + i) / i
            }
            return res
        }
    }
}
