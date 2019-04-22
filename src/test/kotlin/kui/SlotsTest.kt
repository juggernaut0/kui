package kui

import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals

class SlotsTest {
    @Test
    fun singleSlot() {
        val slotted = object : SlottedComponent<Unit>() {
            override fun render() {
                markup().div {
                    h1 { +"Some title" }
                    slot(Unit)
                    small { +"Some footer" }
                }
            }
        }
        val root = componentOf {
            it.component(slotted) {
                slot(Unit) {
                    p { +"Hello world" }
                }
            }
        }

        val elem = document.createElement("div")
        mountComponent(elem, root)

        assertEquals("<div><h1>Some title</h1><p>Hello world</p><small>Some footer</small></div>", elem.innerHTML)
    }

    @Test
    fun multiSlot() {
        val slotted = object : SlottedComponent<Int>() {
            override fun render() {
                markup().div {
                    h1 { +"Some title" }
                    slot(0)
                    h2 { +"Subtitle" }
                    slot(1)
                    small { +"Some footer" }
                }
            }
        }
        val root = componentOf {
            it.component(slotted) {
                slot(0) {
                    p { +"Hello world" }
                }
                slot(1) {
                    p { +"Body" }
                }
            }
        }

        val elem = document.createElement("div")
        mountComponent(elem, root)

        assertEquals("<div><h1>Some title</h1><p>Hello world</p><h2>Subtitle</h2><p>Body</p><small>Some footer</small></div>", elem.innerHTML)
    }
}
