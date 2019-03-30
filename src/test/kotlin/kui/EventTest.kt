package kui

import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventTest {
    @Test
    fun removeListener() {
        val comp = object : Component() {
            var disabled = false
            var count = 0

            fun handleClick() {
                count++
                disabled = true
                render()
            }

            override fun render() {
                if (disabled) {
                    markup().button { +"click" }
                } else {
                    markup().button(Props(click = { handleClick() })) { +"click" }
                }
            }
        }

        val elem = document.createElement("div")
        mountComponent(elem, comp)

        assertEquals("<button>click</button>", elem.innerHTML)
        assertFalse(comp.disabled)
        assertEquals(0, comp.count)

        val button = elem.firstElementChild!! as HTMLButtonElement
        button.click()

        assertTrue(comp.disabled)
        assertEquals(1, comp.count)

        val button2 = elem.firstElementChild!! as HTMLButtonElement
        button2.click()

        assertTrue(comp.disabled)
        assertEquals(1, comp.count)
    }
}