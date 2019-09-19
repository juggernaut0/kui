package kui

import kui.Props.Companion.empty
import kotlin.js.Date
import kotlin.reflect.KMutableProperty0

@DslMarker
annotation class MarkupDsl

@Suppress("unused", "NOTHING_TO_INLINE")
@MarkupDsl
sealed class MarkupBuilder {
    abstract fun add(node: KuiNode)

    inline fun htmlElement(tag: String, props: Props = empty, block: MarkupBuilder.() -> Unit) {
        val elem = SimpleKuiElement(tag, props)
        ElementMarkupBuilder(elem).block()
        add(elem)
    }

    inline fun a(props: Props = empty, href: String = "#", block: MarkupBuilder.() -> Unit)
            = htmlElement("a", props.copy(attrs = props.attrs + ("href" to href)), block)
    inline fun b(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("b", props, block)
    inline fun button(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("button", props, block)
    inline fun code(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("code", props, block)
    inline fun div(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("div", props, block)
    inline fun em(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("em", props, block)
    inline fun form(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("form", props, block)
    inline fun h1(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h1", props, block)
    inline fun h2(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h2", props, block)
    inline fun h3(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h3", props, block)
    inline fun h4(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h4", props, block)
    inline fun h5(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h5", props, block)
    inline fun h6(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h6", props, block)
    inline fun hr(props: Props = empty) = htmlElement("hr", props) { }
    inline fun i(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("i", props, block)
    fun img(props: Props = empty, src: String? = null, alt: String? = null)
            = htmlElement("img", props.withAttrs("src" to src, "alt" to alt)) { }

    fun inputText(props: Props = empty, placeholder: String? = null, model: KMutableProperty0<String>? = null)
            = add(InputTextKuiElement(props, placeholder, model))
    fun inputNumber(props: Props = empty, placeholder: String? = null, model: KMutableProperty0<Double>? = null)
            = add(InputNumberKuiElement(props, placeholder, model))
    fun checkbox(props: Props = empty, model: KMutableProperty0<Boolean>? = null)
            = add(CheckboxKuiElement(props, model))
    fun <T> radio(props: Props = empty, name: String, value: T, model: KMutableProperty0<T>? = null)
            = add(RadioKuiElement(props, name, value, model))
    fun inputDate(props: Props = empty, model: KMutableProperty0<Date>? = null)
            = add(InputDateKuiElement(props, model))

    inline fun label(props: Props = empty, forId: String? = null, block: MarkupBuilder.() -> Unit) {
        val realProps = if (forId != null) props.copy(attrs = props.attrs + ("for" to forId)) else props
        htmlElement("label", realProps, block)
    }

    inline fun li(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("li", props, block)
    inline fun nav(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("nav", props, block)
    inline fun ol(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("ol", props, block)
    inline fun p(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("p", props, block)
    inline fun pre(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("pre", props, block)

    fun <T : Any> select(props: Props = empty, options: List<T> = emptyList(), model: KMutableProperty0<T>? = null)
            = add(SelectKuiElement(props, options, model?.toModel()))
    fun <T : Any> select(props: Props = empty, options: List<T> = emptyList(), nullOption: String = "", model: KMutableProperty0<T?>? = null) {
        val optsWithNull = mutableListOf<OptionWrapper<T>>(NullOption(nullOption))
        options.mapTo(optsWithNull) { ValueOption(it) }

        val delModel = model?.let { m ->
            object : ModelProperty<OptionWrapper<T>> {
                override fun get(): OptionWrapper<T> = m.get()?.let { ValueOption(it) } ?: NullOption("")
                override fun set(t: OptionWrapper<T>) = m.set(t.value)
            }
        }

        add(SelectKuiElement(props, optsWithNull, delModel))
    }

    inline fun small(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("small", props, block)
    inline fun span(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("span", props, block)
    inline fun strong(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("strong", props, block)
    inline fun table(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("table", props, block)
    inline fun thead(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("thead", props, block)
    inline fun tbody(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("tbody", props, block)
    inline fun tr(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("tr", props, block)
    inline fun th(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("th", props, block)
    inline fun td(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("td", props, block)

    fun textarea(props: Props = empty, model: KMutableProperty0<String>?) = add(TextAreaKuiElement(props, model))

    inline fun ul(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("ul", props, block)

    operator fun String.unaryPlus() {
        add(KuiTextNode(this))
    }

    fun component(component: Component) {
        component.renderInternal()
        if (component.rootElement.isSet) {
            add(component.rootElement)
        }
    }

    inline fun <T> component(component: SlottedComponent<T>, slots: SlotBuilder<T>.() -> Unit) {
        SlotBuilder(component).apply(slots)
        component(component)
    }
}

internal class RootMarkupBuilder(private val component: Component, private val internal: Boolean) : MarkupBuilder() {
    override fun add(node: KuiNode) {
        if (internal) {
            component.rootElement = KuiComponentNode(node)
        } else {
            if (component.rootElement.isSet) {
                node.renderAgainst(component.rootElement.get())
            }
            component.rootElement.set(node)
        }
    }
}

class ElementMarkupBuilder(private val parent: KuiElement) : MarkupBuilder() {
    override fun add(node: KuiNode) {
        parent.addChild(node)
    }
}

@MarkupDsl
class SlotBuilder<T>(private val comp: SlottedComponent<T>) {
    init {
        comp.shouldClearOnRenderInternal = false
    }

    fun slot(slot: T, markup: MarkupBuilder.() -> Unit) {
        comp.slots[slot] = markup
    }
}

private interface OptionWrapper<out T : Any> {
    val value: T?
}
private class ValueOption<T : Any>(override val value: T) : OptionWrapper<T> {
    override fun toString(): String = value.toString()
    override fun equals(other: Any?): Boolean = other is ValueOption<*> && other.value == value
    override fun hashCode(): Int = 31 * value.hashCode()
}
private class NullOption(private val def: String) : OptionWrapper<Nothing> {
    override val value: Nothing? get() = null
    override fun toString(): String = def
    override fun equals(other: Any?): Boolean = other is NullOption
    override fun hashCode(): Int = 31 * this::class.js.hashCode()
}
