package org.calcfx.app

import org.calcfx.app.engine.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class MathEngineTest {

    private val evaluator = MathEvaluator()

    @Test
    fun testBasicArithmetic() {
        assertEquals(5.0, evaluator.evaluate("2+3").decimalValue, 1e-9)
        assertEquals(-1.0, evaluator.evaluate("2-3").decimalValue, 1e-9)
        assertEquals(6.0, evaluator.evaluate("2*3").decimalValue, 1e-9)
        assertEquals(2.5, evaluator.evaluate("5/2").decimalValue, 1e-9)
    }

    @Test
    fun testOperatorPrecedence() {
        assertEquals(14.0, evaluator.evaluate("2+3*4").decimalValue, 1e-9)
        assertEquals(20.0, evaluator.evaluate("(2+3)*4").decimalValue, 1e-9)
        assertEquals(-4.0, evaluator.evaluate("-2^2").decimalValue, 1e-9)
        assertEquals(4.0, evaluator.evaluate("(-2)^2").decimalValue, 1e-9)
        assertEquals(0.25, evaluator.evaluate("2^-2").decimalValue, 1e-9)
        assertEquals(512.0, evaluator.evaluate("2^3^2").decimalValue, 1e-9)
    }

    @Test
    fun testNegativeNumbers() {
        assertEquals(-2.0, evaluator.evaluate("-5+3").decimalValue, 1e-9)
        assertEquals(-6.0, evaluator.evaluate("3*-2").decimalValue, 1e-9)
        assertEquals(6.0, evaluator.evaluate("-3*-2").decimalValue, 1e-9)
        assertEquals(8.0, evaluator.evaluate("5--3").decimalValue, 1e-9)
        assertEquals(-5.0, evaluator.evaluate("-5").decimalValue, 1e-9)
    }

    @Test
    fun testImplicitMultiplication() {
        assertEquals(10.0, evaluator.evaluate("2(5)").decimalValue, 1e-9)
        assertEquals(14.0, evaluator.evaluate("2(3+4)").decimalValue, 1e-9)
        assertEquals(10.0, evaluator.evaluate("(2)(5)").decimalValue, 1e-9)
    }

    @Test
    fun testTrigonometryDegree() {
        evaluator.angleUnit = AngleUnit.DEGREE
        assertEquals(0.5, evaluator.evaluate("sin(30)").decimalValue, 1e-7)
        assertEquals(0.5, evaluator.evaluate("cos(60)").decimalValue, 1e-7)
        assertEquals(1.0, evaluator.evaluate("tan(45)").decimalValue, 1e-7)
        assertEquals(30.0, evaluator.evaluate("asin(0.5)").decimalValue, 1e-7)
        assertEquals(60.0, evaluator.evaluate("acos(0.5)").decimalValue, 1e-7)
        assertEquals(45.0, evaluator.evaluate("atan(1)").decimalValue, 1e-7)
    }

    @Test
    fun testTrigonometryRadian() {
        evaluator.angleUnit = AngleUnit.RADIAN
        assertEquals(0.0, evaluator.evaluate("sin(π)").decimalValue, 1e-7)
        assertEquals(-1.0, evaluator.evaluate("cos(π)").decimalValue, 1e-7)
    }

    @Test
    fun testHyperbolicFunctions() {
        assertEquals(0.0, evaluator.evaluate("sinh(0)").decimalValue, 1e-9)
        assertEquals(1.0, evaluator.evaluate("cosh(0)").decimalValue, 1e-9)
        assertEquals(0.0, evaluator.evaluate("tanh(0)").decimalValue, 1e-9)
    }

    @Test
    fun testLogarithmsAndRoots() {
        assertEquals(2.0, evaluator.evaluate("log(100)").decimalValue, 1e-9)
        assertEquals(1.0, evaluator.evaluate("ln(e)").decimalValue, 1e-9)
        assertEquals(4.0, evaluator.evaluate("sqrt(16)").decimalValue, 1e-9)
        assertEquals(3.0, evaluator.evaluate("cbrt(27)").decimalValue, 1e-9)
        assertEquals(5.0, evaluator.evaluate("abs(-5)").decimalValue, 1e-9)
    }

    @Test
    fun testFactorialAndCombinatorics() {
        assertEquals(120.0, evaluator.evaluate("5!").decimalValue, 1e-9)
        assertEquals(1.0, evaluator.evaluate("0!").decimalValue, 1e-9)
        assertEquals(20.0, evaluator.evaluate("5nPr2").decimalValue, 1e-9)
        assertEquals(10.0, evaluator.evaluate("5nCr2").decimalValue, 1e-9)
        assertEquals(20.0, evaluator.evaluate("5P2").decimalValue, 1e-9)
    }

    @Test
    fun testFractionsAndRadicals() {
        val res1 = evaluator.evaluate("0.75")
        assertEquals("3/4", res1.exactFraction?.toFormattedString())

        val res2 = evaluator.evaluate("1.5")
        assertEquals("3/2", res2.exactFraction?.toFormattedString())

        val res3 = evaluator.evaluate("sqrt(12)")
        assertEquals("2√3", res3.exactRadical)
    }

    @Test
    fun testCalculusEngine() {
        // Derivative of x^2 at x = 3 is 6
        val d = CalculusEngine.derivative("X^2", "X", 3.0) { expr, x ->
            evaluator.evaluateWithVariable(expr, 'X', x)
        }
        assertEquals(6.0, d, 1e-4)

        // Integral of x from 0 to 2 is 2
        val i = CalculusEngine.integrate("X", "X", 0.0, 2.0, 1000) { expr, x ->
            evaluator.evaluateWithVariable(expr, 'X', x)
        }
        assertEquals(2.0, i, 1e-4)

        // Sum of X from 1 to 10 is 55
        val s = CalculusEngine.summation("X", "X", 1, 10) { expr, x ->
            evaluator.evaluateWithVariable(expr, 'X', x)
        }
        assertEquals(55.0, s, 1e-9)
    }

    @Test
    fun testEquationSolver() {
        // 2x2: x + y = 3, 2x - y = 0 => x=1, y=2
        val sol2x2 = EquationSolver.solveLinear2x2(1.0, 1.0, 3.0, 2.0, -1.0, 0.0)
        assertTrue(sol2x2 is EquationSolution.Linear2D)
        val l2 = sol2x2 as EquationSolution.Linear2D
        assertEquals(1.0, l2.x, 1e-9)
        assertEquals(2.0, l2.y, 1e-9)

        // Quadratic: x^2 - 5x + 6 = 0 => x=3, x=2
        val quad = EquationSolver.solveQuadratic(1.0, -5.0, 6.0)
        assertTrue(quad is EquationSolution.Quadratic)
        val q = quad as EquationSolution.Quadratic
        assertEquals(3.0, q.x1.real, 1e-9)
        assertEquals(2.0, q.x2.real, 1e-9)
    }

    @Test
    fun testMatrixEngine() {
        val a = Matrix(2, 2, arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0)))
        assertEquals(-2.0, a.determinant(), 1e-9)

        val inv = a.inverse()
        val prod = a * inv
        assertEquals(1.0, prod[0, 0], 1e-9)
        assertEquals(0.0, prod[0, 1], 1e-9)
        assertEquals(0.0, prod[1, 0], 1e-9)
        assertEquals(1.0, prod[1, 1], 1e-9)
    }

    @Test
    fun testStatisticsEngine() {
        val data = listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)
        val stats = StatisticsEngine.calculate1Var(data)
        assertNotNull(stats)
        assertEquals(5.0, stats!!.mean, 1e-9)
        assertEquals(4.5, stats.median, 1e-9)
        assertEquals(2.0, stats.min, 1e-9)
        assertEquals(9.0, stats.max, 1e-9)

        val x = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
        val y = listOf(2.0, 4.0, 6.0, 8.0, 10.0)
        val stats2 = StatisticsEngine.calculate2Var(x, y)
        assertNotNull(stats2)
        assertEquals(2.0, stats2!!.slopeB, 1e-9)
        assertEquals(0.0, stats2.interceptA, 1e-9)
        assertEquals(1.0, stats2.correlationR, 1e-9)
    }

    @Test
    fun testSmallNumberDivision() {
        val res = evaluator.evaluate("1 / 0.0000000000000001")
        assertFalse(res.isError)
        assertEquals(1e16, res.decimalValue, 1e10)
    }

    @Test
    fun testRadicalSimplification() {
        val r1 = evaluator.evaluate("sqrt(18)")
        assertEquals("3√2", r1.exactRadical)

        val r2 = evaluator.evaluate("sqrt(48)")
        assertEquals("4√3", r2.exactRadical)
    }

    @Test
    fun testComplexArithmetic() {
        val c1 = ComplexNumber(3.0, 4.0)
        val c2 = ComplexNumber(1.0, -2.0)

        val sum = c1 + c2
        assertEquals(4.0, sum.real, 1e-9)
        assertEquals(2.0, sum.imag, 1e-9)

        val prod = c1 * c2
        assertEquals(11.0, prod.real, 1e-9)
        assertEquals(-2.0, prod.imag, 1e-9)

        val mag = c1.magnitude
        assertEquals(5.0, mag, 1e-9)
    }

    @Test
    fun testUnitConverter() {
        val kmhToMs = UnitConverter.convert(100.0, 19) // km/h -> m/s
        assertEquals(27.77777777777778, kmhToMs, 1e-6)

        val cToF = UnitConverter.convert(100.0, 40) // °C -> °F
        assertEquals(212.0, cToF, 1e-6)

        val inchToCm = UnitConverter.convert(1.0, 1) // in -> cm
        assertEquals(2.54, inchToCm, 1e-6)
    }

    @Test
    fun testErrorHandling() {
        val divZero = evaluator.evaluate("5/0")
        assertTrue(divZero.isError)

        val unknownFn = evaluator.evaluate("unknown(5)")
        assertTrue(unknownFn.isError)

        val invalidNum = evaluator.evaluate("1.2e+")
        assertTrue(invalidNum.isError)
    }
}
