package org.calcfx.app.engine

import java.math.BigInteger
import kotlin.math.abs

/**
 * Exact Rational Arithmetic implementation for Natural Textbook Fraction Display.
 */
data class Fraction(val numerator: BigInteger, val denominator: BigInteger) : Comparable<Fraction> {

    init {
        require(denominator != BigInteger.ZERO) { "Denominator cannot be zero" }
    }

    companion object {
        val ZERO = Fraction(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Fraction(BigInteger.ONE, BigInteger.ONE)

        fun of(num: Long, den: Long = 1L): Fraction {
            return Fraction(BigInteger.valueOf(num), BigInteger.valueOf(den)).simplify()
        }

        fun fromDouble(value: Double, maxDenominator: Long = 1000000L): Fraction? {
            if (value.isNaN() || value.isInfinite()) return null
            if (abs(value) < 1e-12) return ZERO

            val isNegative = value < 0
            val positiveVal = abs(value)

            var h1 = 1L; var h2 = 0L
            var k1 = 0L; var k2 = 1L
            var b = positiveVal

            do {
                val a = b.toLong()
                var aux = h1
                h1 = a * h1 + h2
                h2 = aux
                aux = k1
                k1 = a * k1 + k2
                k2 = aux
                if (b - a < 1e-12) break
                b = 1.0 / (b - a)
            } while (abs(positiveVal - h1.toDouble() / k1.toDouble()) > positiveVal * 1e-11 && k1 < maxDenominator)

            val num = if (isNegative) -h1 else h1
            return of(num, k1)
        }
    }

    fun simplify(): Fraction {
        if (numerator == BigInteger.ZERO) return ZERO
        val gcd = numerator.gcd(denominator)
        var num = numerator / gcd
        var den = denominator / gcd
        if (den < BigInteger.ZERO) {
            num = -num
            den = -den
        }
        return Fraction(num, den)
    }

    operator fun plus(other: Fraction): Fraction {
        val num = (numerator * other.denominator) + (other.numerator * denominator)
        val den = denominator * other.denominator
        return Fraction(num, den).simplify()
    }

    operator fun minus(other: Fraction): Fraction {
        val num = (numerator * other.denominator) - (other.numerator * denominator)
        val den = denominator * other.denominator
        return Fraction(num, den).simplify()
    }

    operator fun times(other: Fraction): Fraction {
        val num = numerator * other.numerator
        val den = denominator * other.denominator
        return Fraction(num, den).simplify()
    }

    operator fun div(other: Fraction): Fraction {
        require(other.numerator != BigInteger.ZERO) { "Division by zero fraction" }
        val num = numerator * other.denominator
        val den = denominator * other.numerator
        return Fraction(num, den).simplify()
    }

    fun reciprocal(): Fraction = Fraction(denominator, numerator).simplify()

    fun toDouble(): Double = numerator.toDouble() / denominator.toDouble()

    fun isInteger(): Boolean = denominator == BigInteger.ONE

    fun toFormattedString(): String {
        val simplified = simplify()
        return if (simplified.isInteger()) {
            simplified.numerator.toString()
        } else {
            "${simplified.numerator}/${simplified.denominator}"
        }
    }

    fun toMixedString(): String {
        val simplified = simplify()
        if (simplified.isInteger()) return simplified.numerator.toString()
        val whole = simplified.numerator / simplified.denominator
        val rem = (simplified.numerator % simplified.denominator).abs()
        return if (whole == BigInteger.ZERO) {
            "${simplified.numerator}/${simplified.denominator}"
        } else {
            "$whole $rem/${simplified.denominator}"
        }
    }

    override fun compareTo(other: Fraction): Int {
        return (numerator * other.denominator).compareTo(other.numerator * denominator)
    }
}
