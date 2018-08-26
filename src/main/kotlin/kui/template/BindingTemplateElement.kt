package kui.template

import kui.Component
import org.w3c.dom.Element
import org.w3c.dom.Node
import kotlin.browser.document

class BindingTemplateElement<C : Component>(private val binding: Binding<C, String>) : TemplateElement<C>() {
    override fun render(component: C, parent: Element): List<Node> {
        val text = document.createTextNode(binding(component))
        parent.appendChild(text)
        return listOf(text)
    }
}
