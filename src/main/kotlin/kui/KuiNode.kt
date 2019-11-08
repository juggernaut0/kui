package kui

import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.dom.clear
import kotlin.js.Date
import kotlin.reflect.KMutableProperty0

interface KuiNode {
    val node: Node?
    fun render(): Node
    fun renderAgainst(existing: KuiNode): Node
}

class KuiTextNode(private val data: String) : KuiNode {
    private var text: Text? = null
    override val node: Node? get() = text

    override fun render(): Node = document.createTextNode(data).also { text = it }

    override fun renderAgainst(existing: KuiNode): Node {
        if (existing is KuiTextNode && existing.text != null) {
            val oldNode = existing.text!!
            if (oldNode.data != data) {
                oldNode.data = data
            }
            text = oldNode
        } else {
            existing.node?.let { it.parentElement?.replaceChild(render(), it) }
        }
        // both branches must set node
        return text!!
    }
}

abstract class KuiElement(private val tag: String, private val props: Props) : KuiNode {
    private val _children = mutableListOf<KuiNode>()
    val children: List<KuiNode> get() = _children

    private var element: HTMLElement? = null
    override val node: Node? get() = element
    private val events: MutableMap<String, EventListener> = mutableMapOf()

    fun addChild(node: KuiNode) {
        _children.add(node)
    }

    override fun render(): Node {
        // populateElement will set element
        val node = populateElement(document.createElement(tag) as HTMLElement)
        for (child in children) {
            node.appendChild(child.render())
        }
        return node
    }

    override fun renderAgainst(existing: KuiNode): Node {
        if (existing is KuiElement && canReuse(existing)) {
            val elem = populateElement(existing.element!!, existing)

            var lastUnchanged: Node? = null
            var lastAdded: Node? = null

            for (d in diff(existing.children, children, ::elemEq)) {
                when (d) {
                    is Unchanged -> {
                        lastUnchanged = d.new.renderAgainst(d.old)
                        lastAdded = null
                    }
                    is Added -> {
                        val child = when {
                            lastAdded != null -> lastAdded.nextSibling
                            lastUnchanged != null -> lastUnchanged.nextSibling
                            else -> elem.firstChild
                        }
                        val e = d.new.render()
                        elem.insertBefore(e, child)
                        lastAdded = e
                    }
                    is Removed -> d.old.node?.let { elem.removeChild(it) }
                }
            }
        } else {
            val n = render()
            existing.node?.let { it.parentElement?.replaceChild(n, it) }
        }
        // Both sides must set element
        return element!!
    }

    private fun populateElement(elem: HTMLElement, existing: KuiElement? = null): Element {
        if (existing == null || props.id != existing.props.id) {
            if (props.id != null) elem.id = props.id else elem.removeAttribute("id")
        }

        if (existing == null || props.classes != existing.props.classes) {
            if (props.classes.isNotEmpty()) {
                elem.className = props.classes.joinToString(separator = " ")
            } else {
                elem.removeAttribute("class")
            }
        }

        if (existing == null || props.title != existing.props.title) {
            if (props.title != null) {
                elem.title = props.title
            } else {
                elem.removeAttribute("title")
            }
        }

        if (existing != null) {
            for ((name, _) in existing.props.attrs) {
                if (name !in props.attrs) {
                    elem.removeAttribute(name)
                }
            }
        }
        for ((name, value) in props.attrs) {
            elem.setAttribute(name, value)
        }

        setEventListener(elem, existing, "blur", props.blur)
        setEventListener(elem, existing, "click", props.click)
        setEventListener(elem, existing, "focus", props.focus)
        setKeyEventListener(elem, existing, "keydown", props.keydown)
        setKeyEventListener(elem, existing, "keyup", props.keyup)
        setEventListener(elem, existing, "mousedown", props.mousedown)
        setEventListener(elem, existing, "mouseup", props.mouseup)
        setEventListener(elem, existing, "mouseenter", props.mouseenter)
        setEventListener(elem, existing, "mouseleave", props.mouseleave)
        setEventListener(elem, existing, "mousemove", props.mousemove)

        if (tag in disableableTags) {
            // No common subclass for things that can have disable
            if (existing == null || props.disabled != existing.props.disabled) {
                elem.asDynamic().disabled = props.disabled
            }
        }

        if (existing == null || props.tabIndex != existing.props.tabIndex) {
            if (props.tabIndex != null) {
                elem.tabIndex = props.tabIndex
            } else {
                elem.removeAttribute("tabindex")
            }
        }

        customizeElement(elem, existing)

        element = elem
        return elem
    }

    protected abstract fun customizeElement(elem: Element, existing: KuiElement?)

    protected fun swapEventListener(event: String, elem: Element, existing: KuiElement?, newListener: (Event) -> Unit) {
        clearEventListeners(event, elem, existing)
        val listener = EventListener(newListener)
        events[event] = listener
        elem.addEventListener(event, listener)
    }

    protected fun clearEventListeners(event: String, elem: Element, existing: KuiElement?) {
        if (existing != null) elem.removeEventListener(event, existing.events[event])
    }

    private fun setEventListener(elem: Element, existing: KuiElement?, event: String, handler: (() -> Unit)?) {
        if (handler != null) {
            swapEventListener(event, elem, existing) { handler() }
        } else {
            clearEventListeners(event, elem, existing)
        }
    }

    private fun setKeyEventListener(elem: Element, existing: KuiElement?, event: String, handler: ((KeyboardEventArgs) -> Unit)?) {
        if (handler != null) {
            swapEventListener(event, elem, existing) { e ->
                handler(KeyboardEventArgs(e as KeyboardEvent))
            }
        } else {
            clearEventListeners(event, elem, existing)
        }
    }

    private fun canReuse(existing: KuiElement): Boolean {
        return tag == existing.tag && props.id == existing.props.id && existing.element != null
    }

    private fun elemEq(old: KuiNode, new: KuiNode): Boolean {
        return if (old is KuiTextNode && new is KuiTextNode) true
        else if (old is KuiElement && new is KuiElement) new.canReuse(old)
        else if (old is KuiComponentNode && new is KuiComponentNode) elemEq(old.get(), new.get())
        else false
    }

    companion object {
        private val disableableTags = setOf("button", "input", "select", "textarea")
    }
}

class SimpleKuiElement(tag: String, props: Props) : KuiElement(tag, props) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        // empty
    }
}

class InputTextKuiElement(props: Props, placeholder: String?, private val model: KMutableProperty0<String>?)
    : KuiElement("input", props.withAttrs("type" to "text", "placeholder" to placeholder)) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        if (model != null) {
            (elem as HTMLInputElement).value = model.get()
            swapEventListener("input", elem, existing) { e -> (e.target as? HTMLInputElement)?.let { model.set(it.value) } }
        } else {
            clearEventListeners("input", elem, existing)
        }
    }
}

class InputNumberKuiElement(
        props: Props,
        type: String,
        placeholder: String?,
        min: Double?,
        max: Double?,
        step: Double?,
        private val model: KMutableProperty0<Double>?
) : KuiElement("input", props.withAttrs(
        "type" to type,
        "placeholder" to placeholder,
        "min" to min?.toString(),
        "max" to max?.toString(),
        "step" to step?.toString()
)) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        if (model != null) {
            (elem as HTMLInputElement).value = model.get().toString()
            swapEventListener("input", elem, existing) { e ->
                (e.target as? HTMLInputElement)?.value?.toDoubleOrNull()?.let { model.set(it) }
            }
        } else {
            clearEventListeners("input", elem, existing)
        }
    }
}

class CheckboxKuiElement(props: Props, private val model: KMutableProperty0<Boolean>?)
    : KuiElement("input", props.copy(attrs = props.attrs + ("type" to "checkbox"))) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        if (model != null) {
            (elem as HTMLInputElement).checked = model.get()
            swapEventListener("change", elem, existing) { e ->
                (e.target as? HTMLInputElement)?.let { model.set(it.checked) }
            }
        } else {
            clearEventListeners("change", elem, existing)
        }
    }
}

class RadioKuiElement<T>(props: Props, name: String, private val value: T, private val model: KMutableProperty0<T>?)
    : KuiElement("input", props.copy(attrs = props.attrs + listOf("type" to "radio", "name" to name))) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        if (model != null) {
            (elem as HTMLInputElement).checked = value == model.get()
            // change is only called when radio is selected, NOT unselected
            swapEventListener("change", elem, existing) { model.set(value) }
        } else {
            clearEventListeners("change", elem, existing)
        }
    }
}

class SelectKuiElement<T>(props: Props, private val options: List<T> = emptyList(), private val model: ModelProperty<T>?)
    : KuiElement("select", props) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        elem as HTMLSelectElement

        elem.clear()
        for (opt in options) {
            val optElem = document.createElement("option") as HTMLOptionElement
            optElem.text = opt.toString()
            elem.add(optElem)
        }

        if (model != null) {
            // -1 = no selection
            elem.selectedIndex = options.indexOf(model.get())
            swapEventListener("change", elem, existing) { e ->
                (e.target as? HTMLSelectElement)?.let { model.set(options[it.selectedIndex]) }
            }
        } else {
            clearEventListeners("change", elem, existing)
        }
    }
}

class InputDateKuiElement(props: Props, private val model: KMutableProperty0<Date>?)
    : KuiElement("input", props.withAttrs("type" to "date")) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        if (model != null) {
            (elem as HTMLInputElement).value = model.get().toISODateString()
            swapEventListener("input", elem, existing) { e ->
                (e.target as? HTMLInputElement)?.value?.toDateOrNull()?.let { model.set(it) }
            }
        } else {
            clearEventListeners("input", elem, existing)
        }
    }
}

class TextAreaKuiElement(props: Props, private val model: KMutableProperty0<String>?) : KuiElement("textarea", props) {
    override fun customizeElement(elem: Element, existing: KuiElement?) {
        if (model != null) {
            (elem as HTMLTextAreaElement).value = model.get()
            swapEventListener("input", elem, existing) { e -> (e.target as? HTMLTextAreaElement)?.let { model.set(it.value) } }
        } else {
            clearEventListeners("input", elem, existing)
        }
    }
}

class KuiComponentNode(private var inner: KuiNode? = null) : KuiNode {
    val isSet get() = inner != null

    fun get(): KuiNode = ensureSet()

    fun set(value: KuiNode) {
        inner = value
    }

    fun unset() {
        inner = null
    }

    override val node: Node? get() = ensureSet().node

    override fun render(): Node = ensureSet().render()

    override fun renderAgainst(existing: KuiNode): Node {
        val other = if (existing is KuiComponentNode) existing.get() else existing
        return ensureSet().renderAgainst(other)
    }

    private fun ensureSet(): KuiNode {
        if (inner == null) throw IllegalStateException("Cannot render when unset")
        return inner!!
    }
}
