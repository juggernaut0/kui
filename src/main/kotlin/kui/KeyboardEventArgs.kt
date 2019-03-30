package kui

import org.w3c.dom.events.KeyboardEvent

class KeyboardEventArgs internal constructor(private val event: KeyboardEvent) {
    val key get() = event.key
    val code get() = event.code
    val isCtrlDown get() = event.ctrlKey
    val isShiftDown get() = event.shiftKey
    val isAltDown get() = event.altKey
    val isMetadown get() = event.altKey
    val isRepeat get() = event.repeat
}
