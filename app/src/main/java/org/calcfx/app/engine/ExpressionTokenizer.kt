package org.calcfx.app.engine

sealed class Token {
    data class Number(val value: Double, val text: String) : Token()
    data class Variable(val name: Char) : Token()
    data class Function(val name: String) : Token()
    data class Operator(val op: Char) : Token()
    object OpenParen : Token()
    object CloseParen : Token()
    object Comma : Token()
    object Ans : Token()
    object Factorial : Token()
    object Percent : Token()
}

class ExpressionTokenizer(private val input: String) {
    private var pos = 0

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (pos < input.length) {
            val c = input[pos]
            when {
                c.isWhitespace() -> pos++
                c.isDigit() || c == '.' -> {
                    tokens.add(readNumber())
                }
                c == '(' -> { tokens.add(Token.OpenParen); pos++ }
                c == ')' -> { tokens.add(Token.CloseParen); pos++ }
                c == ',' -> { tokens.add(Token.Comma); pos++ }
                c == '!' -> { tokens.add(Token.Factorial); pos++ }
                c == '%' -> { tokens.add(Token.Percent); pos++ }
                c in "+-*/^×÷−" -> {
                    val normalized = when (c) {
                        '×' -> '*'
                        '÷' -> '/'
                        '−' -> '-'
                        else -> c
                    }
                    tokens.add(Token.Operator(normalized))
                    pos++
                }
                c == 'π' || c == 'e' -> {
                    val value = if (c == 'π') Math.PI else Math.E
                    tokens.add(Token.Number(value, c.toString()))
                    pos++
                }
                c.isLetter() -> {
                    val ident = readIdentifier()
                    when (ident) {
                        "Ans", "ans" -> tokens.add(Token.Ans)
                        "pi", "PI" -> tokens.add(Token.Number(Math.PI, "π"))
                        "e" -> tokens.add(Token.Number(Math.E, "e"))
                        "i" -> tokens.add(Token.Variable('i'))
                        in setOf("sin", "cos", "tan", "asin", "acos", "atan",
                                 "sinh", "cosh", "tanh", "asinh", "acosh", "atanh",
                                 "log", "ln", "sqrt", "cbrt", "abs", "Pol", "Rec", "int", "diff") -> {
                            tokens.add(Token.Function(ident))
                        }
                        "nPr", "P" -> tokens.add(Token.Operator('P'))
                        "nCr" -> tokens.add(Token.Operator('C'))
                        else -> {
                            if (ident.length == 1) {
                                tokens.add(Token.Variable(ident[0]))
                            } else {
                                tokens.add(Token.Function(ident))
                            }
                        }
                    }
                }
                else -> pos++
            }
        }
        return tokens
    }

    private fun readNumber(): Token.Number {
        val start = pos
        var hasDot = false
        while (pos < input.length) {
            val c = input[pos]
            if (c.isDigit()) {
                pos++
            } else if (c == '.' && !hasDot) {
                hasDot = true
                pos++
            } else if ((c == 'E' || c == 'e') && pos + 1 < input.length &&
                (input[pos + 1].isDigit() || input[pos + 1] == '-' || input[pos + 1] == '+')) {
                pos += 2
                while (pos < input.length && input[pos].isDigit()) pos++
                break
            } else {
                break
            }
        }
        val text = input.substring(start, pos)
        val value = text.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $text")
        return Token.Number(value, text)
    }

    private fun readIdentifier(): String {
        val start = pos
        while (pos < input.length && (input[pos].isLetter() || input[pos] == '_')) {
            pos++
        }
        return input.substring(start, pos)
    }
}
