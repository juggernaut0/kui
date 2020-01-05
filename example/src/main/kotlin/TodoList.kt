import kui.*

class TodoListComponent : Component() {
    private var nextItem: String = ""
    private val items: MutableList<TodoListItem> = mutableListOf()
    private var selectedItem: TodoListItem? by renderOnSet(null)

    private fun addItem() {
        items.add(TodoListItem(nextItem))
        nextItem = ""
        render()
    }

    private fun removeItem() {
        val item = selectedItem ?: return
        items.remove(item)
        selectedItem = null
        render()
    }

    override fun render() {
        markup().div {
            inputText(model = ::nextItem)
            button(Props(click = ::addItem)) { +"Add Item" }
            p(classes(TodoStyles.greenSmall)) {
                +"Size: ${items.size}"
            }
            select(options = items, model = ::selectedItem, nullOption = "None")
            button(Props(
                    classes = if (selectedItem == null) emptyList() else listOf(TodoStyles.red),
                    click = ::removeItem,
                    disabled = selectedItem == null
            )) { +"Delete" }
            ul {
                for(item in items) {
                    component(TodoListItemComponent(item))
                }
            }
        }
    }
}

data class TodoListItem(val content: String, var done: Boolean = false)

class TodoListItemComponent(private val item: TodoListItem) : Component() {
    private var checked: Boolean
        get() = item.done
        set(value) {
            item.done = value
            render()
        }

    override fun render() {
        markup().li {
            checkbox(model = ::checked)
            span(Props(classes = if (checked) listOf(TodoStyles.lineThrough) else emptyList())) {
                +item.content
            }
        }
    }
}

object TodoStyles {
    val red by styleClass { "color: red;" }
    val lineThrough by styleClass { "text-decoration: line-through; color: #666" }
    val greenSmall by mediaStyleClass {
        media("only screen and (max-width: 900px)") {
            "color: green;"
        }
        default {
            "color: black;"
        }
    }
}
