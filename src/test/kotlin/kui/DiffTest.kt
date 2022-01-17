package kui

import kotlin.test.Test
import kotlin.test.assertEquals

class DiffTest {
    private fun <T> apply(start: List<T>, diffs: List<Diff<T>>): List<T> {
        var i = 0
        val result = start.toMutableList()
        for (diff in diffs) {
            when (diff) {
                is Added -> {
                    result.add(i, diff.new)
                    i++
                }
                is Unchanged -> i++
                is Removed -> result.removeAt(i)
            }
        }
        return result
    }

    @Test
    fun testSimple() {
        val a = listOf(1, 2, 2, 3)
        val b = listOf(1, 2, 3, 4)

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testToEmpty() {
        val a = listOf(1, 2, 3)
        val b = emptyList<Int>()

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testFromEmpty() {
        val a = emptyList<Int>()
        val b = listOf(1, 2, 3)

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testAddAtEnd() {
        val a = listOf(1)
        val b = listOf(1, 1)

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testRemoveAtEnd() {
        val a = listOf(1, 1)
        val b = listOf(1)

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testHuge() {
        val a = (1..5000).toList()
        val b = a.toMutableList()
        b.add(500, -1)

        val diffs = diff(a, b)
        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testDifferent() {
        val a = listOf(1, 2, 3)
        val b = listOf(4, 5, 6)

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }

    @Test
    fun testAlmostDifferent() {
        val a = listOf(1, 2, 3, 0)
        val b = listOf(4, 5, 6, 0)

        val diffs = diff(a, b)

        println(diffs)

        assertEquals(b, apply(a, diffs))
    }
}
