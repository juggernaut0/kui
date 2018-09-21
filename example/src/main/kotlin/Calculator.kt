import kui.Component
import kui.renderOnSet

class CalcComponent : Component() {
    private val resultComponent = CalcResultComponent(this)

    private var num1 by renderOnSet(0.0, resultComponent)
    private var num2 by renderOnSet(0.0, resultComponent)
    private var op by renderOnSet(Op.ADD, resultComponent)

    fun result(): Double {
        return when (op) {
            Op.ADD -> num1 + num2
            Op.SUB -> num1 - num2
            Op.MUL -> num1 * num2
            Op.DIV -> num1 / num2
        }
    }

    override fun render() {
        markup().div {
            inputNumber(model = ::num1)
            div {
                label { +"+"; radio(name = "calc", value = Op.ADD, model = ::op) }
                label { +"-"; radio(name = "calc", value = Op.SUB, model = ::op) }
                label { +"*"; radio(name = "calc", value = Op.MUL, model = ::op) }
                label { +"/"; radio(name = "calc", value = Op.DIV, model = ::op) }
            }
            inputNumber(model = ::num2)
            component(resultComponent)
        }
    }
}

class CalcResultComponent(private val parent: CalcComponent) : Component() {
    override fun render() {
        markup().p { +"Result: ${parent.result()}" }
    }
}

enum class Op { ADD, SUB, MUL, DIV }
