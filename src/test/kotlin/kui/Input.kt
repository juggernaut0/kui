package kui

import org.w3c.dom.EventInit
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class Input {
    private fun HTMLElement.setValue(value: String) {
        this.asDynamic().value = value
        dispatchEvent(Event("input", EventInit(bubbles = true)))
    }

    private fun assertDateEquals(expected: Date, actual: Date) {
        assertEquals(expected.getTime(), actual.getTime(), "Expected <$expected>, actual <$actual>")
    }

    @Test
    fun dateInput() {
        val elem = document.createElement("div")
        val comp = object : Component() {
            var value = Date(2010, 7, 21)
            override fun render() {
                markup().inputDate(Props(id = "x"), model = ::value)
            }
        }
        mountComponent(elem, comp)

        val inputElem = elem.querySelector("#x") as HTMLInputElement
        assertEquals("2010-08-21", inputElem.value)

        comp.value = Date(2019, 0, 1)
        comp.render()

        val inputElem2 = elem.querySelector("#x") as HTMLInputElement
        assertEquals("2019-01-01", inputElem2.value)

        inputElem2.setValue("1999-12-30")
        assertDateEquals(Date(1999, 11, 30), comp.value)
    }

    @Test
    fun textarea() {
        val elem = document.createElement("div")
        val comp = object : Component() {
            var value = "Hello"
            override fun render() {
                markup().textarea(Props(id = "x"), model = ::value)
            }
        }
        mountComponent(elem, comp)

        val inputElem = elem.querySelector("#x") as HTMLTextAreaElement
        assertEquals("Hello", inputElem.value)

        comp.value = "Testing\n1 2 3 4"
        comp.render()

        val inputElem2 = elem.querySelector("#x") as HTMLTextAreaElement
        assertEquals("Testing\n1 2 3 4", inputElem2.value)

        inputElem2.setValue("5 6 7 8")
        assertEquals("5 6 7 8", comp.value)
    }

    @Test
    fun range() {
        val elem = document.createElement("div")
        val comp = object : Component() {
            var value = 50.0
            override fun render() {
                markup().inputRange(Props(id = "x"), min = 0.0, max = 100.0, model = ::value)
            }
        }
        mountComponent(elem, comp)

        val inputElem = elem.querySelector("#x") as HTMLInputElement
        assertEquals("50", inputElem.value)

        comp.value = 75.0
        comp.render()

        val inputElem2 = elem.querySelector("#x") as HTMLInputElement
        assertEquals("75", inputElem2.value)

        inputElem2.setValue("25")
        assertEquals(25.0, comp.value)
    }
}