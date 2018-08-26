package kui

import kui.template.Template
import org.w3c.dom.Element

abstract class Component {
    internal var mountPoint: Element? = null
    internal var rootElement: Element? = null
}

interface Renderer<C : Component> {
    val template: Template<C>

    fun render(component: C) {
        template.render(component)
    }
}
