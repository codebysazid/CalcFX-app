package org.calcfx.app.engine

import kotlin.math.*

sealed class EquationSolution {
    data class Linear2D(val x: Double, val y: Double) : EquationSolution()
    data class Linear3D(val x: Double, val y: Double, val z: Double) : EquationSolution()
    data class Quadratic(
        val x1: ComplexNumber,
        val x2: ComplexNumber,
        val vertexX: Double,
        val vertexY: Double,
        val isMinimum: Boolean
    ) : EquationSolution()
    data class Cubic(
        val x1: ComplexNumber,
        val x2: ComplexNumber,
        val x3: ComplexNumber
    ) : EquationSolution()
    data class Error(val message: String) : EquationSolution()
}

object EquationSolver {

    /**
     * Solves a 2x2 linear system:
     * a1*x + b1*y = c1
     * a2*x + b2*y = c2
     */
    fun solveLinear2x2(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double
    ): EquationSolution {
        val det = a1 * b2 - a2 * b1
        if (abs(det) < 1e-12) {
            return EquationSolution.Error("No unique solution (Det = 0)")
        }
        val x = (c1 * b2 - c2 * b1) / det
        val y = (a1 * c2 - a2 * c1) / det
        return EquationSolution.Linear2D(x, y)
    }

    /**
     * Solves a 3x3 linear system:
     * a1*x + b1*y + c1*z = d1
     * a2*x + b2*y + c2*z = d2
     * a3*x + b3*y + c3*z = d3
     */
    fun solveLinear3x3(
        a1: Double, b1: Double, c1: Double, d1: Double,
        a2: Double, b2: Double, c2: Double, d2: Double,
        a3: Double, b3: Double, c3: Double, d3: Double
    ): EquationSolution {
        val det = a1 * (b2 * c3 - b3 * c2) - b1 * (a2 * c3 - a3 * c2) + c1 * (a2 * b3 - a3 * b2)
        if (abs(det) < 1e-12) {
            return EquationSolution.Error("No unique solution (Det = 0)")
        }

        val detX = d1 * (b2 * c3 - b3 * c2) - b1 * (d2 * c3 - d3 * c2) + c1 * (d2 * b3 - d3 * b2)
        val detY = a1 * (d2 * c3 - d3 * c2) - d1 * (a2 * c3 - a3 * c2) + c1 * (a2 * d3 - a3 * d2)
        val detZ = a1 * (b2 * d3 - b3 * d2) - b1 * (a2 * d3 - a3 * d2) + d1 * (a2 * b3 - a3 * b2)

        return EquationSolution.Linear3D(detX / det, detY / det, detZ / det)
    }

    /**
     * Solves ax^2 + bx + c = 0
     */
    fun solveQuadratic(a: Double, b: Double, c: Double): EquationSolution {
        if (abs(a) < 1e-12) {
            if (abs(b) < 1e-12) return EquationSolution.Error("Invalid equation: a = 0 and b = 0")
            val root = -c / b
            return EquationSolution.Quadratic(
                x1 = ComplexNumber(root, 0.0),
                x2 = ComplexNumber(root, 0.0),
                vertexX = root,
                vertexY = 0.0,
                isMinimum = true
            )
        }

        val discriminant = b * b - 4 * a * c
        val vertexX = -b / (2 * a)
        val vertexY = c - (b * b) / (4 * a)
        val isMin = a > 0

        return if (discriminant >= 0) {
            val sqrtD = sqrt(discriminant)
            val r1 = (-b + sqrtD) / (2 * a)
            val r2 = (-b - sqrtD) / (2 * a)
            EquationSolution.Quadratic(
                x1 = ComplexNumber(r1, 0.0),
                x2 = ComplexNumber(r2, 0.0),
                vertexX = vertexX,
                vertexY = vertexY,
                isMinimum = isMin
            )
        } else {
            val sqrtD = sqrt(-discriminant)
            val realPart = -b / (2 * a)
            val imagPart = sqrtD / (2 * a)
            EquationSolution.Quadratic(
                x1 = ComplexNumber(realPart, imagPart),
                x2 = ComplexNumber(realPart, -imagPart),
                vertexX = vertexX,
                vertexY = vertexY,
                isMinimum = isMin
            )
        }
    }

    /**
     * Solves ax^3 + bx^2 + cx + d = 0 using Cardano's formula
     */
    fun solveCubic(a: Double, b: Double, c: Double, d: Double): EquationSolution {
        if (abs(a) < 1e-12) {
            return solveQuadratic(b, c, d)
        }

        // Depressed cubic: t^3 + pt + q = 0 where x = t - b/(3a)
        val p = (3 * a * c - b * b) / (3 * a * a)
        val q = (2 * b * b * b - 9 * a * b * c + 27 * a * a * d) / (27 * a * a * a)
        val shift = b / (3 * a)

        val discriminant = (q * q / 4.0) + (p * p * p / 27.0)

        return if (discriminant > 0) {
            // 1 Real root, 2 Complex conjugate roots
            val u = cbrt(-q / 2.0 + sqrt(discriminant))
            val v = cbrt(-q / 2.0 - sqrt(discriminant))
            val t1 = u + v
            val x1 = t1 - shift

            val realPart = -(u + v) / 2.0 - shift
            val imagPart = (u - v) * sqrt(3.0) / 2.0

            EquationSolution.Cubic(
                x1 = ComplexNumber(x1, 0.0),
                x2 = ComplexNumber(realPart, imagPart),
                x3 = ComplexNumber(realPart, -imagPart)
            )
        } else if (abs(discriminant) < 1e-12) {
            // All roots real, at least 2 are equal
            val u = cbrt(-q / 2.0)
            val t1 = 2 * u
            val t2 = -u
            EquationSolution.Cubic(
                x1 = ComplexNumber(t1 - shift, 0.0),
                x2 = ComplexNumber(t2 - shift, 0.0),
                x3 = ComplexNumber(t2 - shift, 0.0)
            )
        } else {
            // 3 distinct real roots (casus irreducibilis)
            val r = sqrt(-p * p * p / 27.0)
            val phi = acos(-q / (2.0 * r))
            val m = 2.0 * cbrt(r)

            val x1 = m * cos(phi / 3.0) - shift
            val x2 = m * cos((phi + 2 * Math.PI) / 3.0) - shift
            val x3 = m * cos((phi + 4 * Math.PI) / 3.0) - shift

            EquationSolution.Cubic(
                x1 = ComplexNumber(x1, 0.0),
                x2 = ComplexNumber(x2, 0.0),
                x3 = ComplexNumber(x3, 0.0)
            )
        }
    }

    private fun cbrt(x: Double): Double = if (x >= 0) x.pow(1.0 / 3.0) else -(-x).pow(1.0 / 3.0)
}
