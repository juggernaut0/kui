package kui.template

import kui.Component
import kui.ComponentFactory
import org.w3c.dom.Element
import org.w3c.dom.Node

class ComponentTemplateElement<C : Component, C2 : Component, D>(
        private val factory: ComponentFactory<C2>,
        private val initializer: C2.(D?) -> Unit,
        private val dataBinding: Binding<C, D>? = null
) : TemplateElement<C>() {
    override fun render(component: C, parent: Element): List<Node> {
        val data = dataBinding?.invoke(component)
        return factory.render(parent, init = { initializer(data) })
    }
}
