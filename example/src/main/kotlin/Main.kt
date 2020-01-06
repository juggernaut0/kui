import kui.componentOf
import kui.mountComponent
import kotlin.browser.document

fun main() {
    mountComponent("app", TodoListComponent())
    mountComponent("calc", CalcComponent())
    mountComponent("events", EventsDemo())
    mountComponent(document.body!!, componentOf {
        it.div {
            h3 { +"Sign In Form" }
            form {
                label {
                    +"Email"
                    inputText(autocomplete = "email")
                }
                label {
                    +"Password"
                    inputPassword(autocomplete = "current-password")
                }
                button { +"Submit" }
            }
        }
    })
}
