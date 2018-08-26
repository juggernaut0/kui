import kui.Component
import kui.Kui
import kui.Renderer
import kui.template.*

class TodoListComponent : Component() {
    private var nextItem: String = ""
    private val items: MutableList<TodoListItem> = mutableListOf()

    private fun addItem() {
        items.add(TodoListItem(nextItem))
        nextItem = ""
        render(this)
    }

    companion object : Renderer<TodoListComponent> {
        override val template: Template<TodoListComponent> = buildTemplate {
            div {
                input(type = InputType.TEXT, model = InBinding(TodoListComponent::nextItem))
                button(click = TodoListComponent::addItem) { +"Add Item" }
                p {
                    bind { it.items.size.toString() }
                }
                ul {
                    foreach(Binding(TodoListComponent::items)) { item ->
                        component(TodoListItemComponent, item) {
                            this.item = it
                        }
                    }
                }
            }
        }
    }
}

data class TodoListItem(val content: String)

class TodoListItemComponent : Component() {
    var item: TodoListItem? = null

    companion object : Renderer<TodoListItemComponent> {
        override val template: Template<TodoListItemComponent> = buildTemplate {
            li {
                bind { it.item?.content ?: "" }
            }
        }
    }
}

fun main(args: Array<String>) {
    // TODO remove when compiler inline bug is fixed
    Kui::class
    HtmlTemplateElement<TodoListItemComponent>("li")

    Kui("todo")
            .rootComponent(TodoListComponent)
            .start()
}
