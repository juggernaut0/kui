import kui.mountComponent

fun main(args: Array<String>) {
    mountComponent("app", TodoListComponent())
    mountComponent("calc", CalcComponent())
}
