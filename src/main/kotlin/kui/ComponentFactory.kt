package kui

import org.w3c.dom.Element
import org.w3c.dom.Node
import kotlin.reflect.KClass

class ComponentFactory<C : Component>(kClass: KClass<C>, private val renderer: Renderer<C>) {
    @Suppress("unused")
    @JsName("jsCls")
    private val jsCls = kClass.js

    fun render(mountPoint: Element, init: (C.() -> Unit)? = null): List<Node> {
        // Instantiate root component
        // TODO dependency injection
        val component = js("new this.jsCls()") as C
        if (init != null) component.init()
        component.mountPoint = mountPoint
        renderer.render(component)
        return component.rootElement?.let { listOf(it) } ?: emptyList()
    }
}
