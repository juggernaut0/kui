package kui.template

import kui.Component
import org.w3c.dom.Element
import org.w3c.dom.Node
import kotlin.browser.document

class TextTemplateElement<C : Component>(private val text: String) : TemplateElement<C>() {
    override fun render(component: C, parent: Element): List<Node> {
        val text = document.createTextNode(text)
        parent.appendChild(text)
        return listOf(text)
    }
}
