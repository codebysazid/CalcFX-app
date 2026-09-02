package org.calcfx.app.engine

import kotlin.math.sqrt

data class OneVarStats(
    val n: Int,
    val mean: Double,
    val sumX: Double,
    val sumX2: Double,
    val populationStdDev: Double,
    val sampleStdDev: Double,
    val min: Double,
    val max: Double,
    val median: Double
)

data class TwoVarStats(
    val n: Int,
    val meanX: Double,
    val meanY: Double,
    val sumX: Double,
    val sumY: Double,
    val sumX2: Double,
    val sumY2: Double,
    val sumXY: Double,
    val interceptA: Double,
    val slopeB: Double,
    val correlationR: Double,
    val rSquared: Double
)

object StatisticsEngine {

    fun calculate1Var(values: List<Double>): OneVarStats? {
        if (values.isEmpty()) return null
        val n = values.size
        val sumX = values.sum()
        val sumX2 = values.sumOf { it * it }
        val mean = sumX / n

        val popVariance = values.sumOf { (it - mean) * (it - mean) } / n
        val popStdDev = sqrt(popVariance)

        val sampleVariance = if (n > 1) values.sumOf { (it - mean) * (it - mean) } / (n - 1) else 0.0
        val sampleStdDev = sqrt(sampleVariance)

        val sorted = values.sorted()
        val min = sorted.first()
        val max = sorted.last()
        val median = if (n % 2 == 1) {
            sorted[n / 2]
        } else {
            (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        }

        return OneVarStats(
            n = n,
            mean = mean,
            sumX = sumX,
            sumX2 = sumX2,
            populationStdDev = popStdDev,
            sampleStdDev = sampleStdDev,
            min = min,
            max = max,
            median = median
        )
    }

    fun calculate2Var(xValues: List<Double>, yValues: List<Double>): TwoVarStats? {
        val n = minOf(xValues.size, yValues.size)
        if (n < 2) return null

        val sumX = xValues.take(n).sum()
        val sumY = yValues.take(n).sum()
        val sumX2 = xValues.take(n).sumOf { it * it }
        val sumY2 = yValues.take(n).sumOf { it * it }
        val sumXY = (0 until n).sumOf { xValues[it] * yValues[it] }

        val meanX = sumX / n
        val meanY = sumY / n

        // Two-pass algorithm for numerical stability (avoids catastrophic cancellation)
        val ssXX = xValues.take(n).sumOf { (it - meanX) * (it - meanX) }
        val ssYY = yValues.take(n).sumOf { (it - meanY) * (it - meanY) }
        val ssXY = (0 until n).sumOf { (xValues[it] - meanX) * (yValues[it] - meanY) }

        val slopeB = if (ssXX != 0.0) ssXY / ssXX else 0.0
        val interceptA = meanY - slopeB * meanX

        val denom = sqrt(ssXX * ssYY)
        val correlationR = if (denom != 0.0) ssXY / denom else 0.0
        val rSquared = correlationR * correlationR

        return TwoVarStats(
            n = n,
            meanX = meanX,
            meanY = meanY,
            sumX = sumX,
            sumY = sumY,
            sumX2 = sumX2,
            sumY2 = sumY2,
            sumXY = sumXY,
            interceptA = interceptA,
            slopeB = slopeB,
            correlationR = correlationR,
            rSquared = rSquared
        )
    }
}
