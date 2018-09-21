package kui

import kui.Props.Companion.empty
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.Node
import kotlin.browser.document
import kotlin.reflect.KMutableProperty0

@DslMarker
annotation class MarkupDsl

@MarkupDsl
sealed class AbstractMarkupBuilder {
    abstract fun add(node: Node)

    fun makeElement(tag: String, props: Props): Element {
        val elem = document.createElement(tag)
        props.id?.let { elem.id = it }
        props.classes.joinToString(separator = " ").takeIf { it.isNotEmpty() }?.let { elem.className = it }
        for ((name, value) in props.attrs) {
            elem.setAttribute(name, value)
        }
        if (props.click != null) {
            val click = props.click
            elem.addEventListener("click", { _ -> click() })
        }
        return elem
    }

    inline fun htmlElement(tag: String, props: Props = empty, block: MarkupBuilder.() -> Unit) {
        val elem = makeElement(tag, props)
        MarkupBuilder(elem).block()
        add(elem)
    }

    inline fun button(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("button", props, block)

    inline fun div(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("div", props, block)

    inline fun h1(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h1", props, block)
    inline fun h2(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h2", props, block)
    inline fun h3(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h3", props, block)
    inline fun h4(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h4", props, block)
    inline fun h5(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h5", props, block)
    inline fun h6(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h6", props, block)

    fun inputText(props: Props = empty, model: KMutableProperty0<String>? = null) {
        val elem = makeElement("input", props.copy(attrs = props.attrs + mapOf("type" to "text")))
        if (model != null) {
            (elem as HTMLInputElement).value = model.get()
            elem.addEventListener("input", { e -> (e.target as? HTMLInputElement)?.let { model.set(it.value) } })
        }
        add(elem)
    }

    fun inputNumber(props: Props = empty, model: KMutableProperty0<Double>? = null) {
        val elem = makeElement("input", props.copy(attrs = props.attrs + ("type" to "number")))
        if (model != null) {
            (elem as HTMLInputElement).value = model.get().toString()
            elem.addEventListener("input", { e -> (e.target as? HTMLInputElement)?.value?.toDoubleOrNull()?.let { model.set(it) } })
        }
        add(elem)
    }

    fun checkbox(props: Props = empty, model: KMutableProperty0<Boolean>? = null) {
        val elem = makeElement("input", props.copy(attrs = props.attrs + ("type" to "checkbox")))
        if (model != null) {
            (elem as HTMLInputElement).checked = model.get()
            elem.addEventListener("change", { e -> (e.target as? HTMLInputElement)?.let { model.set(it.checked) } })
        }
        add(elem)
    }

    fun <T> radio(props: Props = empty, name: String, value: T, model: KMutableProperty0<T>? = null) {
        val elem = makeElement("input", props.copy(attrs = props.attrs + listOf("type" to "radio", "name" to name)))
        if (model != null) {
            (elem as HTMLInputElement).checked = value == model.get()
            // change is only called when radio is selected, NOT unselected
            elem.addEventListener("change", { model.set(value) })
        }
        add(elem)
    }

    inline fun label(props: Props = empty, forId: String? = null, block: MarkupBuilder.() -> Unit) {
        val realProps = if (forId != null) props.copy(attrs = props.attrs + ("for" to forId)) else props
        htmlElement("label", realProps, block)
    }

    inline fun li(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("li", props, block)

    inline fun p(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("p", props, block)

    fun <T> select(props: Props = empty, options: List<T> = emptyList(), model: KMutableProperty0<T?>? = null) {
        val elem = makeElement("select", props) as HTMLSelectElement
        for (opt in options) {
            val optElem = document.createElement("option") as HTMLOptionElement
            optElem.text = opt.toString()
            elem.add(optElem)
        }
        if (model != null) {
            // -1 = no selection
            elem.selectedIndex = options.indexOf(model.get())
            elem.addEventListener("change", { e ->
                (e.target as? HTMLSelectElement)?.let { model.set(options[it.selectedIndex]) }
            })
        }
        add(elem)
    }

    inline fun span(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("span", props, block)

    inline fun table(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("table", props, block)
    inline fun thead(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("thead", props, block)
    inline fun tbody(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("tbody", props, block)
    inline fun tr(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("tr", props, block)
    inline fun th(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("th", props, block)
    inline fun td(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("td", props, block)

    inline fun ul(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("ul", props, block)

    operator fun String.unaryPlus() {
        add(document.createTextNode(this))
    }
}

class RootMarkupBuilder(private val component: Component) : AbstractMarkupBuilder() {
    override fun add(node: Node) {
        val element = node as? Element ?: document.createElement("span")
        if (component.rootElement == null) {
            component.mountPoint?.appendChild(element)
        } else {
            component.mountPoint?.replaceChild(element, component.rootElement!!)
        }
        component.rootElement = element
    }
}

class MarkupBuilder(private val parent: Element) : AbstractMarkupBuilder() {
    override fun add(node: Node) {
        parent.appendChild(node)
    }

    fun component(component: Component) {
        component.mountPoint = parent
        component.rootElement = null
        component.render()
    }
}
