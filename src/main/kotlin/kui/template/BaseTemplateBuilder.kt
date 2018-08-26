package kui.template

import kui.Component

class BaseTemplateBuilder<C : Component> : TemplateBuilder<C>() {
    var rootElement: HtmlTemplateElement<C>? = null

    override fun add(element: TemplateElement<C>) {
        if (rootElement != null) throw IllegalStateException("Multiple root level elements in template")
        if (element !is HtmlTemplateElement) throw IllegalArgumentException("Root element must be Html")
        rootElement = element
    }

    fun build(): Template<C> {
        return Template(rootElement ?: HtmlTemplateElement("span"))
    }
}