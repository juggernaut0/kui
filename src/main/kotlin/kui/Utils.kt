package kui

import kotlin.js.Date

internal fun Props.withAttrs(vararg attrs: Pair<String, String?>): Props {
    val a = mutableMapOf<String, String>()
    for ((name, value) in attrs) {
        if (value != null) {
            a[name] = value
        }
    }
    return if (a.isNotEmpty()) copy(attrs = this.attrs + a) else this
}

internal fun Date.toISODateString(): String {
    val mm = (getMonth() + 1).toString().padStart(2, '0')
    val dd = getDate().toString().padStart(2, '0')
    return "${getFullYear()}-$mm-$dd"
}

private val dateRegex = Regex("(\\d{4})-(\\d{2})-(\\d{2})")
internal fun String.toDateOrNull(): Date? {
    val match = dateRegex.matchEntire(this) ?: return null
    val year = match.groupValues[1].toInt()
    val month = match.groupValues[2].toInt() - 1
    val day = match.groupValues[3].toInt()
    return Date(year, month, day)
}
