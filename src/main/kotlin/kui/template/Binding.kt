package kui.template

import kui.Component
import kotlin.reflect.KMutableProperty1

abstract class Binding<C : Component, out R> {
    internal abstract operator fun invoke(component: C): R

    fun <S> map(mapper: (R) -> S): Binding<C, S> = SimpleBinding { mapper(this(it)) }

    companion object {
        operator fun <C : Component, R> invoke(getter: (C) -> R): Binding<C, R> = SimpleBinding(getter)
    }
}

internal class SimpleBinding<C : Component, out R>(private val getter: (C) -> R) : Binding<C, R>() {
    override fun invoke(component: C): R = getter(component)
}

// For foreach blocks
class ItemBinding<C : Component, R>: Binding<C, R>() {
    var clear = true
        private set
    private var item: R? = null

    internal fun set(item: R) {
        this.item = item
        clear = false
    }

    internal fun clear() {
        this.item = null
        clear = true
    }

    override fun invoke(component: C): R {
        return item ?: throw IllegalStateException("Attempting to retrieve item from ItemBinding before it is set")
    }
}

class InBinding<C : Component, R>(internal val setter: (C, R) -> Unit) {
    constructor(prop: KMutableProperty1<C, R>) : this(prop::set)
}
