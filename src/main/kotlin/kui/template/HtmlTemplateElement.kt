package kui.template

import kui.Component
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.InputEvent
import kotlin.browser.document
import kotlin.dom.clear

class HtmlTemplateElement<C : Component>(
        private val htmlTag: String,
        private val attributes: Map<String, String> = emptyMap()
): TemplateElement<C>() {
    private val events: MutableMap<String, (C) -> (Event) -> Unit> = mutableMapOf()

    fun click(handler: (C) -> Unit) {
        events["click"] = { c -> { _ -> handler(c) }}
    }

    fun input(handler: (C, String) -> Unit) {
        events["input"] = { c -> { e ->
            val target = (e as InputEvent).target as? HTMLInputElement
            if (target != null) {
                handler(c, target.value)
            }
        }}
    }

    override fun render(component: C, parent: Element): List<Node> {
        val elem = document.createElement(htmlTag)
        val nodes = renderToExisting(component, elem)
        parent.appendChild(elem)
        return nodes
    }

    fun renderToExisting(component: C, elem: Element): List<Node> {
        elem.clear()
        for ((name, value) in attributes) {
            elem.setAttribute(name, value)
        }
        for ((eventType, handler) in events) {
            elem.addEventListener(eventType, handler(component))
        }
        for (child in children) {
            child.render(component, elem)
        }
        return listOf(elem)
    }
}
