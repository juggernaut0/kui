package kui

data class Props(
        val id: String? = null,
        val classes: List<String> = emptyList(),
        val title: String? = null,
        val disabled: Boolean = false,
        val tabIndex: Int? = null,
        val attrs: Map<String, String> = emptyMap(),

        // events
        val blur: (() -> Unit)? = null,
        val click: (() -> Unit)? = null,
        val focus: (() -> Unit)? = null,
        val keydown: ((KeyboardEventArgs) -> Unit)? = null,
        val keyup: ((KeyboardEventArgs) -> Unit)? = null,
        val mousedown: (() -> Unit)? = null,
        val mouseup: (() -> Unit)? = null,
        val mouseenter: (() -> Unit)? = null,
        val mouseleave: (() -> Unit)? = null,
        val mousemove: (() -> Unit)? = null,

        val ref: ElementRef? = null
) {
    companion object {
        val empty = Props()
    }
}

fun classes(vararg cl: String) = Props(classes = cl.toList())

fun Props.withAttrs(vararg attrs: Pair<String, String?>): Props {
    val a = mutableMapOf<String, String>()
    for ((name, value) in attrs) {
        if (value != null) {
            a[name] = value
        }
    }
    return if (a.isNotEmpty()) copy(attrs = this.attrs + a) else this
}
