package org.calcfx.app.engine

/**
 * 2D Matrix operations (Determinant, Inversion, Transpose, Matrix Math up to 4x4).
 */
data class Matrix(val rows: Int, val cols: Int, val data: Array<DoubleArray>) {

    init {
        require(rows > 0 && cols > 0) { "Matrix dimensions must be positive" }
        require(data.size == rows && data.all { it.size == cols }) { "Data dimensions mismatch" }
    }

    companion object {
        fun identity(size: Int): Matrix {
            val d = Array(size) { i -> DoubleArray(size) { j -> if (i == j) 1.0 else 0.0 } }
            return Matrix(size, size, d)
        }

        fun zeros(rows: Int, cols: Int): Matrix {
            val d = Array(rows) { DoubleArray(cols) { 0.0 } }
            return Matrix(rows, cols, d)
        }
    }

    operator fun get(r: Int, c: Int): Double = data[r][c]
    operator fun set(r: Int, c: Int, v: Double) { data[r][c] = v }

    operator fun plus(other: Matrix): Matrix {
        require(rows == other.rows && cols == other.cols) { "Matrix dimensions must match for addition" }
        val res = Array(rows) { r -> DoubleArray(cols) { c -> data[r][c] + other.data[r][c] } }
        return Matrix(rows, cols, res)
    }

    operator fun minus(other: Matrix): Matrix {
        require(rows == other.rows && cols == other.cols) { "Matrix dimensions must match for subtraction" }
        val res = Array(rows) { r -> DoubleArray(cols) { c -> data[r][c] - other.data[r][c] } }
        return Matrix(rows, cols, res)
    }

    operator fun times(other: Matrix): Matrix {
        require(cols == other.rows) { "Matrix inner dimensions must match for multiplication" }
        val res = Array(rows) { r ->
            DoubleArray(other.cols) { c ->
                var sum = 0.0
                for (k in 0 until cols) {
                    sum += data[r][k] * other.data[k][c]
                }
                sum
            }
        }
        return Matrix(rows, other.cols, res)
    }

    operator fun times(scalar: Double): Matrix {
        val res = Array(rows) { r -> DoubleArray(cols) { c -> data[r][c] * scalar } }
        return Matrix(rows, cols, res)
    }

    fun transpose(): Matrix {
        val res = Array(cols) { c -> DoubleArray(rows) { r -> data[r][c] } }
        return Matrix(cols, rows, res)
    }

    fun determinant(): Double {
        require(rows == cols) { "Determinant only defined for square matrices" }
        return calcDet(data, rows)
    }

    private fun calcDet(mat: Array<DoubleArray>, n: Int): Double {
        if (n == 1) return mat[0][0]
        if (n == 2) return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0]

        var det = 0.0
        var sign = 1.0
        for (i in 0 until n) {
            val sub = Array(n - 1) { DoubleArray(n - 1) }
            for (r in 1 until n) {
                var sc = 0
                for (c in 0 until n) {
                    if (c == i) continue
                    sub[r - 1][sc++] = mat[r][c]
                }
            }
            det += sign * mat[0][i] * calcDet(sub, n - 1)
            sign = -sign
        }
        return det
    }

    fun inverse(): Matrix {
        require(rows == cols) { "Inverse only defined for square matrices" }
        val det = determinant()
        require(kotlin.math.abs(det) > 1e-12) { "Matrix is singular (det = 0)" }

        val adj = Array(rows) { r ->
            DoubleArray(cols) { c ->
                val sub = Array(rows - 1) { DoubleArray(cols - 1) }
                var sr = 0
                for (i in 0 until rows) {
                    if (i == r) continue
                    var sc = 0
                    for (j in 0 until cols) {
                        if (j == c) continue
                        sub[sr][sc++] = data[i][j]
                    }
                    sr++
                }
                val sign = if ((r + c) % 2 == 0) 1.0 else -1.0
                sign * calcDet(sub, rows - 1)
            }
        }
        // Transpose of cofactor matrix is adjugate
        val res = Array(rows) { r ->
            DoubleArray(cols) { c -> adj[c][r] / det }
        }
        return Matrix(rows, cols, res)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Matrix
        if (rows != other.rows || cols != other.cols) return false
        return data.contentDeepEquals(other.data)
    }

    override fun hashCode(): Int = data.contentDeepHashCode()
}
