package kui.template

import kui.Component
import org.w3c.dom.Element
import org.w3c.dom.Node

class ForEachTemplateElement<C : Component, T>(
        private val collectionBinding: Binding<C, Iterable<T>>,
        private val itemBinding: ItemBinding<C, T>
) : TemplateElement<C>() {
    override fun render(component: C, parent: Element): List<Node> {
        if (!itemBinding.clear) throw IllegalStateException("ItemBinding in use")

        val nodes = mutableListOf<Node>()
        for (item in collectionBinding(component)) {
            itemBinding.set(item)

            for (child in children) {
                nodes += child.render(component, parent)
            }
        }

        itemBinding.clear()

        return nodes
    }
}