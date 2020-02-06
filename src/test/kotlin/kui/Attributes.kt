package kui

import kui.test.assertMatchesHtml
import kui.test.render
import kotlin.test.Test

class Attributes {
    @Test
    fun replaceAttrs() {
        val comp = render(object : Component() {
            var state = true

            override fun render() {
                markup().div {
                    if (state) {
                        button(Props(attrs = mapOf("data-foo" to "1"))) { +"Foo" }
                    } else {
                        button { +"Bar" }
                    }
                }
            }
        })

        assertMatchesHtml("<div><button data-foo=\"1\">Foo</button></div>", comp)

        comp.setState {
            state = false
        }

        assertMatchesHtml("<div><button>Bar</button></div>", comp)

        comp.setState {
            state = true
        }

        assertMatchesHtml("<div><button data-foo=\"1\">Foo</button></div>", comp)
    }
}