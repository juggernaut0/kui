package kui.template

import kui.Component
import kui.ComponentFactory
import kui.Renderer
import org.w3c.dom.Element
import org.w3c.dom.Node

@TemplateMarker
abstract class TemplateElement<C : Component> : TemplateBuilder<C>() {
    protected val children: MutableList<TemplateElement<C>> = mutableListOf()

    override fun add(element: TemplateElement<C>) {
        children.add(element)
    }

    internal abstract fun render(component: C, parent: Element): List<Node>

    inline fun <reified C2 : Component> component(renderer: Renderer<C2>, noinline init: C2.() -> Unit) {
        add(ComponentTemplateElement<C, C2, Nothing>(ComponentFactory(C2::class, renderer), { init() }))
    }

    inline fun <reified C2 : Component, D> component(renderer: Renderer<C2>, binding: Binding<C, D>, noinline init: C2.(D) -> Unit) {
        add(ComponentTemplateElement(ComponentFactory(C2::class, renderer), { init(it!!) }, binding))
    }

    fun bind(binding: Binding<C, String>) {
        add(BindingTemplateElement(binding))
    }

    fun bind(getter: (C) -> String) {
        bind(Binding(getter))
    }

    operator fun String.unaryPlus() {
        add(TextTemplateElement(this))
    }

    inline fun <T> foreach(binding: Binding<C, Iterable<T>>, block: TemplateElement<C>.(Binding<C, T>) -> Unit) {
        val itemBinding = ItemBinding<C, T>()
        add(ForEachTemplateElement(binding, itemBinding).apply { block(itemBinding) })
    }
}
