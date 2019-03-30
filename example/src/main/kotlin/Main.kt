import kui.mountComponent

fun main() {
    mountComponent("app", TodoListComponent())
    mountComponent("calc", CalcComponent())
    mountComponent("events", EventsDemo())
}
