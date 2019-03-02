package kui

internal fun Props.withAttrs(vararg attrs: Pair<String, String?>): Props {
    val a = mutableMapOf<String, String>()
    for ((name, value) in attrs) {
        if (value != null) {
            a[name] = value
        }
    }
    return if (a.isNotEmpty()) copy(attrs = this.attrs + a) else this
}
