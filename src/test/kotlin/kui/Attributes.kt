package kui

import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals

class Attributes {
    @Test
    fun replaceAttrs() {
        val elem = document.createElement("div")
        val comp = object : Component() {
            var state = true

            override fun render() {
                markup().div {
                    if (state) {
                        button(Props(attrs = mapOf("data-foo" to "1"))) { +"Foo" }
                    } else {
                        button { +"Bar" }
                    }
                }
            }
        }
        mountComponent(elem, comp)

        assertEquals("<div><button data-foo=\"1\">Foo</button></div>", elem.innerHTML)

        comp.state = false
        comp.render()

        assertEquals("<div><button>Bar</button></div>", elem.innerHTML)

        comp.state = true
        comp.render()

        assertEquals("<div><button data-foo=\"1\">Foo</button></div>", elem.innerHTML)
    }
}