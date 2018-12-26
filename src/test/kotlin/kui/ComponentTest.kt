package kui

import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComponentTest {
    @Test
    fun simple() {
        val elem = document.createElement("div")
        val comp = componentOf {
            it.div {
                +"Hello World"
            }
        }
        mountComponent(elem, comp)

        assertEquals("<div>Hello World</div>", elem.innerHTML)
    }

    @Test
    fun interact() {
        val elem = document.createElement("div")
        val comp = object : Component() {
            var clicked = false

            override fun render() {
                markup().button(Props(click = { clicked = true })) { +"Click me" }
            }
        }
        mountComponent(elem, comp)

        assertEquals("<button>Click me</button>", elem.innerHTML)
        assertFalse(comp.clicked)

        val button = elem.firstElementChild!! as HTMLButtonElement
        button.click()

        assertTrue(comp.clicked)
    }

    @Test
    fun state() {
        val elem = document.createElement("div")
        val comp = object : Component() {
            var i = 0

            fun inc() {
                i++
                render()
            }

            override fun render() {
                markup().div {
                    p { +"$i" }
                    button(Props(id = "btn", click = { inc() })) { +"Inc" }
                }
            }
        }
        mountComponent(elem, comp)

        assertEquals("<div><p>0</p><button id=\"btn\">Inc</button></div>", elem.innerHTML)

        var button = elem.querySelector("#btn") as HTMLButtonElement
        button.click()

        assertEquals("<div><p>1</p><button id=\"btn\">Inc</button></div>", elem.innerHTML)

        button = elem.querySelector("#btn") as HTMLButtonElement
        button.click()

        assertEquals("<div><p>2</p><button id=\"btn\">Inc</button></div>", elem.innerHTML)
    }
}