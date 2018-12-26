package kui

abstract class Component {
    internal val rootElement = KuiComponentNode()
    internal var innerMarkup: (MarkupBuilder.() -> Unit)? = null

    abstract fun render()

    protected fun MarkupBuilder.renderInner() {
        innerMarkup?.invoke(this)
    }

    protected fun markup(): AbstractMarkupBuilder = RootMarkupBuilder(this)
}
