package kui.test

import kui.*
import org.w3c.dom.Element
import org.w3c.dom.EventInit
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import kotlinx.browser.document
import kotlin.test.assertEquals

fun <T: Component> render(component: T): RenderedComponent<T> {
    val elem = document.createElement("div") as HTMLElement
    mountComponent(elem, component)
    return RenderedComponent(component, elem)
}

fun Element.setValue(value: String) {
    asDynamic().value = value
    dispatchEvent(Event("input", EventInit(bubbles = true)))
}

class RenderedComponent<T: Component> internal constructor(
        val component: T,
        val container: HTMLElement
) {
    fun getBySelector(querySelector: String): Element {
        return container.querySelector(querySelector)
                ?: throw IllegalStateException("Could not find element with selector: $querySelector")
    }

    fun getByLabel(label: String): Element {
        return container.querySelectorAll("label")
                .asList()
                .find { it.textContent == label }
                ?.firstChild
                ?.let { it as Element }
                ?: throw IllegalStateException("Could not find an element with label with content: $label")
    }

    inline fun setState(block: T.() -> Unit) {
        with(component, block)
        component.render()
    }
}

fun assertMatchesHtml(expected: String, component: RenderedComponent<*>) {
    assertEquals(expected, component.container.innerHTML)
}

fun assertMatchesMarkup(component: RenderedComponent<*>, node: KuiNode) {
    val renderedNode = node.render()
    if (renderedNode is Element) {
        assertMatchesHtml(renderedNode.outerHTML, component)
    } else {
        assertEquals(renderedNode.textContent, component.container.textContent)
    }
}

inline fun assertMatchesMarkup(component: RenderedComponent<*>, markup: (MarkupBuilder) -> Unit) {
    val root = SimpleKuiElement("div", Props.empty)
    markup(ElementMarkupBuilder(root))
    assertMatchesMarkup(component, root.children.first())
}
