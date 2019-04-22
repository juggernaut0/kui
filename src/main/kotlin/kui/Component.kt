package kui

abstract class Component {
    internal var rootElement = KuiComponentNode()
    private var internalRender = false

    abstract fun render()

    internal open fun renderInternal() {
        internalRender = true
        render()
        internalRender = false
    }

    protected fun markup(): MarkupBuilder = RootMarkupBuilder(this, internalRender)
}

abstract class SlottedComponent<T> : Component() {
    internal val slots = mutableMapOf<T, MarkupBuilder.() -> Unit>()
    internal var shouldClearOnRenderInternal = true

    protected fun MarkupBuilder.slot(slot: T) {
        slots[slot]?.invoke(this)
    }

    override fun renderInternal() {
        if (shouldClearOnRenderInternal) slots.clear()
        shouldClearOnRenderInternal = true
        super.renderInternal()
    }
}
