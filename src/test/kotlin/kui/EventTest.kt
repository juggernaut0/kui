package kui

import kui.test.assertMatchesHtml
import kui.test.render
import org.w3c.dom.HTMLButtonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EventTest {
    @Test
    fun removeListener() {
        val comp = render(object : Component() {
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
        })

        assertMatchesHtml("<button>click</button>", comp)
        assertFalse(comp.component.disabled)
        assertEquals(0, comp.component.count)

        val button = comp.getBySelector("button") as HTMLButtonElement
        button.click()

        assertTrue(comp.component.disabled)
        assertEquals(1, comp.component.count)

        val button2 = comp.getBySelector("button") as HTMLButtonElement
        button2.click()

        assertTrue(comp.component.disabled)
        assertEquals(1, comp.component.count)
    }

    @Test
    fun stopPropagation() {
        val comp = render(object : Component() {
            var parentClicked = false
            var childClicked = false

            override fun render() {
                markup().div(Props(click = { parentClicked = true })) {
                    div {
                        +"Hello"
                        button(Props(id = "child", click = { childClicked = true })) {
                            +"Inner"
                        }
                    }
                }
            }
        })

        val child = comp.getBySelector("#child") as HTMLButtonElement
        child.click()

        assertTrue(comp.component.childClicked)
        assertFalse(comp.component.parentClicked)
    }
}