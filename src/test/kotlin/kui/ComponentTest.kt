package kui

import kui.test.assertMatchesHtml
import kui.test.render
import org.w3c.dom.HTMLButtonElement
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComponentTest {
    @Test
    fun simple() {
        val comp = render(componentOf {
            it.div {
                +"Hello World"
            }
        })

        assertMatchesHtml("<div>Hello World</div>", comp)
    }

    @Test
    fun interact() {
        val comp = render(object : Component() {
            var clicked = false

            override fun render() {
                markup().button(Props(click = { clicked = true })) { +"Click me" }
            }
        })

        assertMatchesHtml("<button>Click me</button>", comp)
        assertFalse(comp.component.clicked)

        val button = comp.getBySelector("button") as HTMLButtonElement
        button.click()

        assertTrue(comp.component.clicked)
    }

    @Test
    fun state() {
        val comp = render(object : Component() {
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
        })

        assertMatchesHtml("<div><p>0</p><button id=\"btn\">Inc</button></div>", comp)

        var button = comp.getBySelector("#btn") as HTMLButtonElement
        button.click()

        assertMatchesHtml("<div><p>1</p><button id=\"btn\">Inc</button></div>", comp)

        button = comp.getBySelector("#btn") as HTMLButtonElement
        button.click()

        assertMatchesHtml("<div><p>2</p><button id=\"btn\">Inc</button></div>", comp)
    }
}