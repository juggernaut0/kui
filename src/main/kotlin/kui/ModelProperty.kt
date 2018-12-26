package kui

import kotlin.reflect.KMutableProperty0

// simplified KMutableProperty0
interface ModelProperty<T> {
    fun get(): T
    fun set(t: T)
}

internal fun <R> KMutableProperty0<R>.toModel() = object : ModelProperty<R> {
    override fun get(): R = this@toModel.get()
    override fun set(t: R) = this@toModel.set(t)
}
