package kui

data class Props(
        val id: String? = null,
        val classes: List<String> = emptyList(),
        val attrs: Map<String, String> = emptyMap(),
        val click: (() -> Unit)? = null
) {
    companion object {
        val empty = Props()
    }
}

fun classes(vararg cl: String) = Props(classes = cl.toList())
