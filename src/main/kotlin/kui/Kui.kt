package kui

import org.w3c.dom.asList
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.reflect.KClass

class Kui(val name: String) {
    private var rootComponent: ComponentFactory<out Component>? = null

    fun start() {
        if (rootComponent == null) throw IllegalStateException("Root component not set")

        // Find <kui> tag with name
        val elems = document.getElementsByTagName("kui").asList()
        val root = elems
                .find { it.hasAttribute("app") && it.attributes["app"]?.value == name }
                ?: throw IllegalStateException("Cannot find root element for app '$name'")

        // Render root component to dom elements
        rootComponent!!.render(root)
    }

    inline fun <reified C : Component> rootComponent(renderer: Renderer<C>): Kui {
        return rootComponent(C::class, renderer)
    }

    fun <C : Component> rootComponent(componentClass: KClass<C>, renderer: Renderer<C>): Kui {
        rootComponent = ComponentFactory(componentClass, renderer)
        return this
    }
}

