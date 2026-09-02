package org.calcfx.app.engine

import kotlin.math.*

/**
 * Complex Number support (Rectangular a+bi and Polar r∠θ).
 */
data class ComplexNumber(val real: Double, val imag: Double) {

    companion object {
        val ZERO = ComplexNumber(0.0, 0.0)
        val ONE = ComplexNumber(1.0, 0.0)
        val I = ComplexNumber(0.0, 1.0)

        fun fromPolar(r: Double, thetaRadians: Double): ComplexNumber {
            return ComplexNumber(r * cos(thetaRadians), r * sin(thetaRadians))
        }
    }

    val magnitude: Double get() = hypot(real, imag)
    val argument: Double get() = atan2(imag, real)
    val argumentDegrees: Double get() = Math.toDegrees(argument)

    operator fun plus(other: ComplexNumber): ComplexNumber =
        ComplexNumber(real + other.real, imag + other.imag)

    operator fun minus(other: ComplexNumber): ComplexNumber =
        ComplexNumber(real - other.real, imag - other.imag)

    operator fun times(other: ComplexNumber): ComplexNumber =
        ComplexNumber(
            real * other.real - imag * other.imag,
            real * other.imag + imag * other.real
        )

    operator fun div(other: ComplexNumber): ComplexNumber {
        val denom = other.real * other.real + other.imag * other.imag
        require(denom != 0.0) { "Division by complex zero" }
        return ComplexNumber(
            (real * other.real + imag * other.imag) / denom,
            (imag * other.real - real * other.imag) / denom
        )
    }

    fun conjugate(): ComplexNumber = ComplexNumber(real, -imag)

    fun toFormattedString(isDegrees: Boolean = true): String {
        val cleanReal = if (abs(real) < 1e-11) 0.0 else real
        val cleanImag = if (abs(imag) < 1e-11) 0.0 else imag

        if (cleanImag == 0.0) {
            return formatDouble(cleanReal)
        }
        if (cleanReal == 0.0) {
            return if (cleanImag == 1.0) "i" else if (cleanImag == -1.0) "-i" else "${formatDouble(cleanImag)}i"
        }
        val sign = if (cleanImag >= 0) "+" else "-"
        val absImag = abs(cleanImag)
        val imagStr = if (absImag == 1.0) "i" else "${formatDouble(absImag)}i"
        return "${formatDouble(cleanReal)} $sign $imagStr"
    }

    fun toPolarString(isDegrees: Boolean = true): String {
        val r = formatDouble(magnitude)
        val angle = if (isDegrees) formatDouble(argumentDegrees) else formatDouble(argument)
        val unit = if (isDegrees) "°" else " rad"
        return "$r ∠ $angle$unit"
    }

    private fun formatDouble(d: Double): String {
        return if (d == d.toLong().toDouble()) {
            d.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.8g", d).replace(Regex("\\.?0+$"), "")
        }
    }
}
