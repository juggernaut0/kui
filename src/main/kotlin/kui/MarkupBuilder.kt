package kui

import kui.Props.Companion.empty
import kotlin.reflect.KMutableProperty0

@DslMarker
annotation class MarkupDsl

@MarkupDsl
sealed class AbstractMarkupBuilder {
    abstract fun add(node: KuiNode)

    inline fun htmlElement(tag: String, props: Props = empty, block: MarkupBuilder.() -> Unit) {
        val elem = SimpleKuiElement(tag, props)
        MarkupBuilder(elem).block()
        add(elem)
    }

    inline fun a(props: Props = empty, href: String = "#", block: MarkupBuilder.() -> Unit)
            = htmlElement("a", props.copy(attrs = props.attrs + ("href" to href)), block)

    inline fun button(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("button", props, block)

    inline fun div(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("div", props, block)

    inline fun h1(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h1", props, block)
    inline fun h2(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h2", props, block)
    inline fun h3(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h3", props, block)
    inline fun h4(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h4", props, block)
    inline fun h5(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h5", props, block)
    inline fun h6(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("h6", props, block)

    fun inputText(props: Props = empty, model: KMutableProperty0<String>? = null) = add(InputTextKuiElement(props, model))
    fun inputNumber(props: Props = empty, model: KMutableProperty0<Double>? = null) = add(InputNumberKuiElement(props, model))
    fun checkbox(props: Props = empty, model: KMutableProperty0<Boolean>? = null) = add(CheckboxKuiElement(props, model))
    fun <T> radio(props: Props = empty, name: String, value: T, model: KMutableProperty0<T>? = null) = add(RadioKuiElement(props, name, value, model))

    inline fun label(props: Props = empty, forId: String? = null, block: MarkupBuilder.() -> Unit) {
        val realProps = if (forId != null) props.copy(attrs = props.attrs + ("for" to forId)) else props
        htmlElement("label", realProps, block)
    }

    inline fun li(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("li", props, block)

    inline fun p(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("p", props, block)

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

    inline fun table(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("table", props, block)
    inline fun thead(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("thead", props, block)
    inline fun tbody(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("tbody", props, block)
    inline fun tr(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("tr", props, block)
    inline fun th(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("th", props, block)
    inline fun td(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("td", props, block)

    inline fun ul(props: Props = empty, block: MarkupBuilder.() -> Unit) = htmlElement("ul", props, block)

    operator fun String.unaryPlus() {
        add(KuiTextNode(this))
    }
}

class RootMarkupBuilder(private val component: Component) : AbstractMarkupBuilder() {
    override fun add(node: KuiNode) {
        if (component.rootElement.isSet) {
            node.renderAgainst(component.rootElement.get())
        }
        component.rootElement.set(node)
    }
}

class MarkupBuilder(private val parent: KuiElement) : AbstractMarkupBuilder() {
    override fun add(node: KuiNode) {
        parent.addChild(node)
    }

    fun component(component: Component, innerMarkup: (MarkupBuilder.() -> Unit)? = null) {
        component.innerMarkup = innerMarkup
        component.render()
        if (component.rootElement.isSet) {
            add(component.rootElement)
        }
    }
}

private interface OptionWrapper<T : Any> {
    val value: T?
}
private class ValueOption<T : Any>(override val value: T) : OptionWrapper<T> {
    override fun toString(): String = value.toString()
    override fun equals(other: Any?): Boolean = other is ValueOption<*> && other.value == value
    override fun hashCode(): Int = 31 * value.hashCode()
}
private class NullOption<T : Any>(private val def: String) : OptionWrapper<T> {
    override val value: T? get() = null
    override fun toString(): String = def
    override fun equals(other: Any?): Boolean = other is NullOption<*>
    override fun hashCode(): Int = 31 * this::class.js.hashCode()
}
