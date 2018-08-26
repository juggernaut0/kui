package kui.template

import kui.Component
import org.w3c.dom.Element

class Template<C : Component> internal constructor(private val rootElement: HtmlTemplateElement<C>) {
    internal fun render(component: C) {
        val compElem = component.rootElement
        if (compElem == null) {
            // HtmlTemplateElement only produces one node as Element
            val (root) = rootElement.render(component, component.mountPoint ?: return)
            component.rootElement = root as Element
        } else {
            rootElement.renderToExisting(component, compElem)
        }
    }
}

inline fun <C : Component> buildTemplate(block: TemplateBuilder<C>.() -> Unit): Template<C> {
    return BaseTemplateBuilder<C>().apply(block).build()
}

@DslMarker
internal annotation class TemplateMarker
