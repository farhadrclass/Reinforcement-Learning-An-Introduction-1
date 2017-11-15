@file:Suppress("NOTHING_TO_INLINE")

package lab.mars.rl.util.matrix

operator fun Double.times(m: Matrix) = m * this
operator fun Double.plus(m: Matrix): Double {
    require(m.rows == 1 && m.cols == 1)
    return this + m[0, 0]
}

class Matrix(val rows: Int, val cols: Int = rows) {
    companion object {
        fun identity(d: Int): Matrix {
            val m = Matrix(d)
            for (i in 0 until d)
                m[i, i] = 1.0
            return m
        }

        inline fun column(d: Int) = Matrix(d, 1)

        fun column(value: DoubleArray): Matrix {
            val m = column(value.size)
            for (a in 0 until m.rows)
                m[a, 0] = value[a]
            return m
        }

        fun column(d: Int, init: (Int) -> Double): Matrix {
            val m = column(d)
            for (a in 0 until m.rows)
                m[a, 0] = init(a)
            return m
        }
    }

    val raw = DoubleArray(rows * cols) { 0.0 }
    operator fun get(a: Int) = raw[a]
    operator fun get(a: Int, b: Int) = raw[a * cols + b]
    operator fun set(a: Int, b: Int, value: Double) {
        raw[a * cols + b] = value
    }

    operator fun set(a: Int, value: Double) {
        raw[a] = value
    }

    infix fun `=`(m: Matrix) {
        require(rows == m.cols && cols == m.rows)
        System.arraycopy(m.raw, 0, raw, 0, raw.size)
    }

    operator fun times(scalar: Double): Matrix {
        val m = copy()
        m *= scalar
        return m
    }

    operator fun times(m: Matrix): Matrix {
        require(rows == m.cols && cols == m.rows)
        val result = Matrix(rows)
        for (a in 0 until rows)
            for (b in 0 until rows) {
                var sum = 0.0
                for (k in 0 until cols)
                    sum += this[a, k] * m[k, b]
                result[a, b] = sum
            }
        return result
    }

    operator fun timesAssign(scalar: Double) {
        for (i in 0..raw.lastIndex)
            raw[i] *= scalar
    }

    val T: Matrix
        get() {
            val m = Matrix(cols, rows)
            for (a in 0 until rows)
                for (b in 0 until cols)
                    m[b, a] = this[a, b]
            return m
        }

    operator fun minus(m: Matrix): Matrix {
        require(rows == m.rows && cols == m.cols)
        val result = copy()
        result -= m
        return result
    }

    operator fun minusAssign(m: Matrix) {
        require(rows == m.rows && cols == m.cols)
        for (i in 0..raw.lastIndex)
            raw[i] -= m.raw[i]
    }

    fun copy(): Matrix {
        val m = Matrix(rows, cols)
        System.arraycopy(raw, 0, m.raw, 0, raw.size)
        return m
    }

    operator fun div(scalar: Double): Matrix {
        val m = copy()
        m /= scalar
        return m
    }

    operator fun divAssign(scalar: Double) {
        for (i in 0..raw.lastIndex)
            raw[i] /= scalar
    }

    operator fun plusAssign(m: Matrix) {
        require(rows == m.rows && cols == m.cols)
        for (i in 0..raw.lastIndex)
            raw[i] += m.raw[i]
    }

    fun asScalar(): Double {
        require(rows == 1 && cols == 1)
        return raw[0]
    }
}