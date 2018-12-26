package kui

fun <T> diff(first: List<T>, second: List<T>, eq: (T, T) -> Boolean = { x, y -> x == y }): List<Diff<T>> {
    val a = first.asReversed()
    val b = second.asReversed()
    val mat = Array(a.size) { IntArray(b.size) }
    val pts = Array(a.size) { _ -> Array(b.size) { Ptr.EMPTY } }

    fun mat(ai: Int, bi: Int): Int = if (ai < 0 || bi < 0) 0 else mat[ai][bi]

    // build matrix
    for ((ai, bi) in a.indices product b.indices) {
        if(eq(a[ai], b[bi])) {
            mat[ai][bi] = mat(ai - 1, bi - 1) + 1
            pts[ai][bi] = Ptr.DIAG
        } else {
            val left = mat(ai - 1, bi)
            val up = mat(ai, bi - 1)
            if (left >= up) {
                mat[ai][bi] = left
                pts[ai][bi] = Ptr.LEFT
            } else {
                mat[ai][bi] = up
                pts[ai][bi] = Ptr.UP
            }
        }
    }

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

sealed class Diff<T>
data class Added<T>(val new: T) : Diff<T>()
data class Removed<T>(val old: T) : Diff<T>()
data class Unchanged<T>(val old: T, val new: T) : Diff<T>()

internal infix fun <T, U> Iterable<T>.product(that: Iterable<U>): List<Pair<T, U>> {
    val result = mutableListOf<Pair<T, U>>()
    for (t in this) {
        for (u in that) {
            result.add(t to u)
        }
    }
    return result
}
