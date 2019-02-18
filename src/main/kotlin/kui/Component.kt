package kui

abstract class Component {
    internal var rootElement = KuiComponentNode()
    internal var innerMarkup: (MarkupBuilder.() -> Unit)? = null
    private var internal = false

    abstract fun render()

    protected fun MarkupBuilder.renderInner() {
        innerMarkup?.invoke(this)
    }

    internal fun renderInternal() {
        internal = true
        render()
        internal = false
    }

    protected fun markup(): AbstractMarkupBuilder = RootMarkupBuilder(this, internal)
}
