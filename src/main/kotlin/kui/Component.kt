package kui

import org.w3c.dom.Element

abstract class Component {
    internal var mountPoint: Element? = null
    internal var rootElement: Element? = null

    abstract fun render()

    fun markup(): AbstractMarkupBuilder = RootMarkupBuilder(this)
}
