package kui

import kotlin.browser.document
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

        val root = document.createElement("div")
        mountComponent(root, comp)

        assertSame(root.querySelector("#target"), comp.ref.get())
    }
}