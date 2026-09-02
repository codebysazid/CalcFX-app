package org.calcfx.app.engine

import kotlin.math.*

object CalculusEngine {

    /**
     * Numerical Derivative using 5-point central difference stencil:
     * f'(x0) ≈ (-f(x0+2h) + 8f(x0+h) - 8f(x0-h) + f(x0-2h)) / (12h)
     */
    fun derivative(
        expression: String,
        variableName: String = "X",
        x0: Double,
        evaluator: (expr: String, xVal: Double) -> Double
    ): Double {
        val h = 1e-5
        val f_p2 = evaluator(expression, x0 + 2 * h)
        val f_p1 = evaluator(expression, x0 + h)
        val f_m1 = evaluator(expression, x0 - h)
        val f_m2 = evaluator(expression, x0 - 2 * h)

        return (-f_p2 + 8.0 * f_p1 - 8.0 * f_m1 + f_m2) / (12.0 * h)
    }

    /**
     * Definite Integration using Adaptive Composite Simpson's 1/3 Rule
     */
    fun integrate(
        expression: String,
        variableName: String = "X",
        lowerBound: Double,
        upperBound: Double,
        intervals: Int = 1000,
        evaluator: (expr: String, xVal: Double) -> Double
    ): Double {
        if (lowerBound == upperBound) return 0.0
        val n = if (intervals % 2 == 0) intervals else intervals + 1
        val h = (upperBound - lowerBound) / n

        var sum = evaluator(expression, lowerBound) + evaluator(expression, upperBound)

        for (i in 1 until n) {
            val x = lowerBound + i * h
            val fx = evaluator(expression, x)
            sum += if (i % 2 == 0) 2.0 * fx else 4.0 * fx
        }

        return sum * (h / 3.0)
    }

    /**
     * Numerical Summation (Sigma): Σ from x=start to end of f(x)
     */
    fun summation(
        expression: String,
        variableName: String = "X",
        start: Int,
        end: Int,
        evaluator: (expr: String, xVal: Double) -> Double
    ): Double {
        if (start > end) return 0.0
        require(end.toLong() - start.toLong() <= 100_000L) { "Summation range too large (max 100,000 terms)" }
        var total = 0.0
        for (x in start..end) {
            total += evaluator(expression, x.toDouble())
        }
        return total
    }
}
