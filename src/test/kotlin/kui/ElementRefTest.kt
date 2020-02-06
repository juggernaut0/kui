package kui

import kui.test.render
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertSame

class ElementRefTest {
    @Test
    fun test() {
        val comp = object : Component() {
            val ref = ElementRef()

            override fun render() {
                markup().div {
                    div(Props(id = "target", ref = ref)) {}
                }
            }
        }

        assertFails {
            comp.ref.get()
        }

        val rendered = render(comp)

        assertSame(rendered.getBySelector("#target"), comp.ref.get())
    }
}