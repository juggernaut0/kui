package kui.template

import kui.Component

abstract class TemplateBuilder<C : Component> {
    abstract fun add(element: TemplateElement<C>)

    inline fun htmlElement(tag: String, block: TemplateElement<C>.() -> Unit) {
        htmlElement(HtmlTemplateElement(tag), block)
    }

    inline fun htmlElement(elem: HtmlTemplateElement<C>, block: TemplateElement<C>.() -> Unit) {
        elem.block()
        add(elem)
    }

    inline fun button(noinline click: (C) -> Unit, block: TemplateElement<C>.() -> Unit) {
        val elem = HtmlTemplateElement<C>("button")
        elem.click(click)
        htmlElement(elem, block)
    }

    inline fun div(block: TemplateElement<C>.() -> Unit) = htmlElement("div", block)

    fun input(type: InputType? = null, model: InBinding<C, String>? = null) {
        val elem = HtmlTemplateElement<C>("input",
                attributes = mapOf("type" to (type?.name ?: "text"))
        )
        if (model != null) {
            elem.input(model.setter)
        }
        htmlElement(elem) {}
    }

    inline fun li(block: TemplateElement<C>.() -> Unit) = htmlElement("li", block)

    inline fun p(block: TemplateElement<C>.() -> Unit) = htmlElement("p", block)

    inline fun ul(block: TemplateElement<C>.() -> Unit) = htmlElement("ul", block)
}
