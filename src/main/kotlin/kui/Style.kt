package kui

import org.w3c.dom.HTMLStyleElement
import org.w3c.dom.asList
import org.w3c.dom.css.*
import kotlin.browser.document
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private val kuiStyleSheet by lazy {
    val styleElem = document.createElement("style") as HTMLStyleElement
    styleElem.type = "text/css"
    document.head?.appendChild(styleElem)
    styleElem.sheet as CSSStyleSheet
}

fun appendCss(css: String) {
    val styleElem = document.createElement("style") as HTMLStyleElement
    styleElem.type = "text/css"
    styleElem.innerHTML = css
    document.head?.appendChild(styleElem)
}

fun registerCssRule(rule: String) {
    kuiStyleSheet.insertRule(rule, 0)
}

fun styleClass(cssFn: () -> String): ReadOnlyProperty<Any, String> = StyleClass(cssFn)
private class StyleClass(private val cssFn: () -> String): ReadOnlyProperty<Any, String> {
    private var className: String? = null

    private fun injectClass(base: KClass<*>, property: KProperty<*>): String {
        val name = "${base.simpleName}__${property.name}"
        val existing = kuiStyleSheet.cssRules.asList().find { it.type == 1.toShort() && (it as CSSStyleRule).selectorText == ".$name" }
        if (existing == null) {
            registerCssRule(".$name { ${cssFn()} }")
        }
        className = name
        return name
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return className ?: injectClass(thisRef::class, property)
    }
}

fun mediaStyleClass(builder: MediaStyleBuilder.() -> Unit): ReadOnlyProperty<Any, String> {
    return MediaStyleClass(MediaStyleBuilder().apply(builder).queries)
}

class MediaStyleBuilder {
    internal val queries: MutableList<Pair<String?, () -> String>> = mutableListOf()

    fun media(query: String, cssFn: () -> String) {
        queries.add(query to cssFn)
    }

    fun default(cssFn: () -> String) {
        queries.add(null to cssFn)
    }
}

private class MediaStyleClass(private val queries: List<Pair<String?, () -> String>>): ReadOnlyProperty<Any, String> {
    private var className: String? = null

    private fun injectClass(base: KClass<*>, property: KProperty<*>): String {
        val name = "${base.simpleName}__${property.name}"
        for ((query, cssFn) in queries) {
            if (query == null) {
                val existing = kuiStyleSheet.cssRules.findStyleRule(".$name")
                if (existing == null) {
                    registerCssRule(".$name { ${cssFn()} }")
                }
            } else {
                val existingMedia = kuiStyleSheet.cssRules.findMediaRule(query)
                if (existingMedia != null) {
                    existingMedia.insertRule(".$name { ${cssFn()} }", 0)
                } else {
                    registerCssRule("@media $query { .$name { ${cssFn()} } }")
                }
            }
        }
        console.log(kuiStyleSheet)
        className = name
        return name
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return className ?: injectClass(thisRef::class, property)
    }
}

private fun CSSRuleList.findStyleRule(selector: String): CSSStyleRule? {
    return asList()
            .mapNotNull { rule -> rule.takeIf { it.type == CSSRule.STYLE_RULE }?.let { it as CSSStyleRule } }
            .find { it.selectorText == selector }
}

private fun CSSRuleList.findMediaRule(query: String): CSSMediaRule? {
    return asList()
            .mapNotNull { rule -> rule.takeIf { it.type == CSSRule.MEDIA_RULE }?.let { it as CSSMediaRule } }
            .find { it.media.mediaText == query }
}
