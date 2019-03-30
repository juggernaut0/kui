import kui.Component
import kui.Props
import kui.renderOnSet

class EventsDemo : Component() {
    private var event: String by renderOnSet("")

    override fun render() {
        markup().div {
            p(Props(
                    tabIndex = 0,

                    click = { event = "click" },
                    blur = { event = "blur" },
                    focus = { event = "focus" },
                    keyup = { event = "keyup: ${it.key}" },
                    keydown = { event = "keydown: ${it.key}" },
                    mouseenter = { event = "mouseenter" },
                    mouseleave = { event = "mouseleave" }
            )) { +"This is an element" }
            p { +event }
        }
    }
}