package kui

import org.w3c.dom.Element
import kotlinx.browser.document
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun mountComponent(element: Element, component: Component) {
    component.rootElement.unset()
    component.render()
    if (component.rootElement.isSet) {
        element.appendChild(component.rootElement.render())
    }
}

fun mountComponent(id: String, component: Component) {
    mountComponent(document.getElementById(id) ?: return, component)
}

fun <T> renderOnSet(value: T, target: Component? = null): ReadWriteProperty<Component, T> {
    return object : ReadWriteProperty<Component, T> {
        private var field: T = value

        override fun getValue(thisRef: Component, property: KProperty<*>): T {
            return field
        }

        override fun setValue(thisRef: Component, property: KProperty<*>, value: T) {
            field = value
            (target ?: thisRef).render()
        }
    }
}

inline fun componentOf(crossinline render: (MarkupBuilder) -> Unit): Component {
    return object : Component() {
        override fun render() {
            render(markup())
        }
    }
}

inline fun <T : Component> T.setState(block: T.() -> Unit) {
    block()
    render()
}
