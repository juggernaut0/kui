package kui

import kui.test.assertMatchesHtml
import kui.test.render
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RepeatedChildren {
    private class Outer : Component() {
        val inners: MutableList<Inner> = mutableListOf()
        private var i = 0

        fun add() {
            inners.add(Inner(i.toString()))
            i++
            render()
        }

        fun remove(i: Inner) {
            inners.remove(i)
            render()
        }

        override fun render() {
            markup().div {
                for (i in inners) {
                    component(i)
                }
            }
        }
    }

    private class Inner(val i: String) : Component() {
        override fun render() {
            markup().p { +i }
        }

        override fun toString(): String {
            return "Inner($i)"
        }
    }

    @Test
    fun addTwiceRemoveTwice() {
        val rendered = render(Outer())
        val comp = rendered.component

        comp.add()
        comp.add()

        val child0 = comp.rootElement.node?.childNodes?.item(0)
        val child1 = comp.rootElement.node?.childNodes?.item(1)

        assertEquals(2, comp.inners.size)
        assertMatchesHtml("<div><p>0</p><p>1</p></div>", rendered)
        assertSame(child0, comp.inners[0].rootElement.node)
        assertSame(child1, comp.inners[1].rootElement.node)

        comp.remove(comp.inners[0])

        val child0a = comp.rootElement.node?.childNodes?.item(0)

        assertEquals(1, comp.inners.size)
        assertMatchesHtml("<div><p>1</p></div>", rendered)
        assertSame(child0a, comp.inners[0].rootElement.node)

        comp.remove(comp.inners[0])

        assertEquals(0, comp.inners.size)
        assertMatchesHtml("<div></div>", rendered)
    }
}