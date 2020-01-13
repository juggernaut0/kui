package kui

import org.w3c.dom.HTMLElement

class ElementRef {
    private var element: HTMLElement? = null

    fun get(): HTMLElement = element ?: throw IllegalStateException("Ref does not yet contain an element")
    internal fun set(element: HTMLElement) {
        this.element = element
    }
}
