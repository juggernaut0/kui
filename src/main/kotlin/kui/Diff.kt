package kui

internal fun <T> diff(first: List<T>, second: List<T>, eq: (T, T) -> Boolean = { x, y -> x == y }): List<Diff<T>> {
    val a = first.asReversed()
    val b = second.asReversed()

    // build matrix
    val pts = buildMat(a, b, eq)

    // backtrack
    val result = mutableListOf<Diff<T>>()

    var ai = a.lastIndex
    var bi = b.lastIndex
    while(ai >= 0 && bi >= 0) {
        when(pts[ai][bi]) {
            Ptr.DIAG -> {
                result.add(Unchanged(a[ai], b[bi]))
                ai--
                bi--
            }
            Ptr.LEFT -> {
                result.add(Removed(a[ai]))
                ai--
            }
            Ptr.UP -> {
                result.add(Added(b[bi]))
                bi--
            }
            Ptr.EMPTY -> error("unfilled matrix $ai $bi")
        }
    }

    while (ai >= 0) {
        result.add(Removed(a[ai]))
        ai--
    }
    while (bi >= 0) {
        result.add(Added(b[bi]))
        bi--
    }

    return result
}

internal enum class Ptr { EMPTY, UP, LEFT, DIAG }

internal sealed class Diff<T>
internal data class Added<T>(val new: T) : Diff<T>()
internal data class Removed<T>(val old: T) : Diff<T>()
internal data class Unchanged<T>(val old: T, val new: T) : Diff<T>()

private fun <T> buildMat(a: List<T>, b: List<T>, eq: (T, T) -> Boolean): Array<Array<Ptr>> {
    val pts = Array(a.size) { Array(b.size) { Ptr.EMPTY } }
    var ai = 0
    var bi = 0

    search@while (ai < a.size && bi < b.size) {
        for (d in 0 until (a.size - ai) + (b.size - bi)) {
            if (bi - 1 >= 0) {
                pts[ai + d][bi - 1] = Ptr.LEFT
            }
            if (ai - 1 >= 0) {
                pts[ai - 1][bi + d] = Ptr.UP
            }

            var x = ai + d
            var y = bi

            while (x >= ai) {
                if (x >= a.size || y >= b.size) {
                    x--
                    y++
                    continue
                }

                if (eq(a[x], b[y])) {
                    pts[x][y] = Ptr.DIAG
                    ai = x + 1
                    bi = y + 1
                    continue@search
                } else {
                    pts[x][y] = Ptr.LEFT
                    x--
                    y++
                }
            }
        }

        break
    }

    if (ai == a.size && ai > 0) {
        val x = ai - 1
        for (y in bi until b.size) {
            pts[x][y] = Ptr.UP
        }
    }
    if (bi == b.size && bi > 0) {
        val y = bi - 1
        for (x in ai until a.size) {
            pts[x][y] = Ptr.LEFT
        }
    }

    return pts
}
