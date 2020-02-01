# Usage

Kui is a utility library for creating, updating and interacting with HTML DOM element subtrees in Kotlin/JS without 
directly needing to access the Javascript DOM API. Instead, Kui exposes an HTML building DSL to create DOM element 
trees in a declarative, idiomatic fashion. However, Kui is much more than a simple HTML builder, as it allows attaching 
event listeners and binding Kotlin properties to elements to easily attach your data model to the DOM.

Kui is *reactive*, in that it does not actively run its own event loop or listen for DOM updates itself. Instead it 
simply attaches your application's event listeners to the DOM, and it is up to the application to tell Kui if and when 
to update.

Kui is not a framework. It makes no assumptions about your application architecture nor does it provide any extra 
utilities commonly used in patterns such as dependency injection or MVP. As such, Kui can be flexible and used with 
your favorite tools and libraries. It can even be used in conjunction with other popular UI frameworks such as React 
AngularJS.

## Components

The core concept of Kui is that of *components*, objects that represent the state of some part of the UI. Components 
can be as simple as a single element, or can be composed of many HTML tags and even other components. A component may 
have *state* which can modify how it is displayed or behaves.

In Kui, components are represented by the `kui.Component` abstract class. To create your own components, create a class 
that extends this class. This will require you to implement a `render` method.

```kotlin
import kui.*

class MyComponent : Component() {
    override fun render() {
    
    }
}
```

### Markup Builder DSL

The `render` method is where you will declare how your component will be displayed in the document. To gain access to 
the HTML markup builder DSL, use the `markup` method, then call a method on the returned `MarkupBuilder` object to 
declare an element. Most HTML elements may have child elements, so the corresponding DSL functions take a block that 
declares child elements. To insert a literal string into the element's content, use the unary plus operator with a 
String expression.

Many DSL functions also take additional parameters that correspond to commonly used HTML attributes used with that 
element.

```kotlin
override fun render() {
    markup().div {
        h1 { +"My Component" }
        div {
            p { +"This is a paragraph of text" }
            a(href = "http://example.com") {
                +"This is a link"
            }
        }
    }
}
```

The above example will produce an element tree equivalent to the following HTML:

```html
<div>
    <h1>My Component</h1>
    <div>
        <p>This is a paragraph of text</p>
        <a href="http://example.com">This is a link</a>
    </div>
</div>
```

**Note**: You should only call `markup` once per `render` method, and you should only invoke a DSL function directly on 
the `markup` builder once. Calling multiple DSL functions may have unexpected behavior!

Keep in mind the render function is plain old Kotlin code! That means normal language constructs like `if`, loops, and 
function calls work as expected. The following example will create a list of only even numbers from 0 to 10:

```kotlin
override fun render() {
    markup().ul {
        for (item in 0..10) {
            if (item % 2 == 0) {
                li { +"$item" }
            }
        }
    }
}
```

### Mounting & Composing Components

There are two primary ways to use a Component class once you have one. The first is to attach it directly to the 
existing DOM via the `kui.mountComponent` function. First, instantiate your component, then pass it to `mountComponent`, 
along with the parent element you would like to attach to.

```kotlin
fun main() {
    kui.mountComponent(document.getElementById("mount-point")!!, MyComponent())
}
```

This will render the component into a DOM element tree and append the result to the specified element.

The second way to use a component is indirectly via another component's `render` method. When the parent component is 
rendered, it will also render the inner component and add it to the resulting element tree.

```kotlin
override fun render() {
    markup().div {
        h1 { +"My Container" }
        component(MyComponent())
    }
}
```

### Props

You may attach metadata and HTML attributes to the component's DOM elements by passing `Props` objects to the markup DSL 
functions. `Props` is simply a container for common element attributes such as `id`, `class`, etc. as well as event 
handler functions for common DOM events such as `click`, `focus`, `keydown`, etc.

```kotlin
override fun render() {
    markup().div {
        h1(Props(classes = listOf("fancy-header"))) { +"Kui button" }
        button(Props(click = { alert("Clicked!") })) {
            +"Click me"
        }
    }
}
```

A shorthand for `Props(classes = listOf(...))` is provided by the `classes` function. For example, the expression 
`classes("a", "b")` is equivalent to `Props(classes = listOf("a", "b"))`.

**Note**: It is recommended to always use the named parameter syntax for clarity and safety. The order of the 
parameters in `Props` may change between minor releases.

### Data binding

Kui supports binding Kotlin properties to HTML input elements. The markup DSL functions for input element types takes a 
`model` parameter which is a `KMutableProperty0`, a Kotlin 
[property reference](https://kotlinlang.org/docs/reference/reflection.html#property-references). If a property 
reference is provided, Kui will automatically add an event listener to the appropriate input event and set the property 
whenever the value changes.

```kotlin
class DataComponent : Component() {
    private var data: String = ""

    override fun render() {
        markup().div {
            inputText(model = ::data)
            button(Props(click = { prinltn("data is $data") }))
        }   
    }
}
```

### State, Updating and Re-rendering

The properties in a component class make up its *state*. As a component's state is changed, it may be desirable to 
update how the component is rendered. For example, the following component should display a different string depending 
on its internal boolean state:

```kotlin
class StatefulComponent : Component() {
    var state: Boolean = false

    override fun render() {
        markup().div {
            if (state) {
                +"Hooray! My state is true!"
            } else {
                +"Awww... My state is false."
            }
        }   
    }
}
```

However, as is, simply setting the state property will not automatically cause the DOM to reflect the new state. You 
must tell Kui that the component is to be re-rendered by calling the component's `render` method. This will regenerate 
the components DOM element tree and replace it in place. For example to make the above component happy, you could pass 
it into this function:

```kotlin
fun update(component: StatefulComponent) {
    component.state = true
    component.render()
}
```

**Note**: The `render` method is *synchronous* and the DOM changes will be applied immediately. However, the browser may 
choose to not repaint the screen until after JS has finished executing.

#### setState

If there are many properties to update at once, a shorthand is provided by the the `setState` method (reminiscent of 
the React [setState method](https://reactjs.org/docs/react-component.html#setstate).) For example:

```kotlin
fun complex(component: SomeComplexComponent) {
    component.state = true
    component.frob = 4
    component.greeting = "Hello Kui!"
    component.render()
}
```

is equivalent to

```kotlin
fun complex(component: SomeComplexComponent) {
    component.setState {
        state = true
        frob = 4
        greeting = "Hello Kui!"
    }
}
```

#### Render on set

If it is desirable to always re-render a component when one if it's properties changed, it may be convenient to provide 
a custom setter that always calls the `render` method. For example, you could modify the `StatefulComponent` above:

```kotlin
class StatefulComponent : Component() {
    var state: Boolean = false
        set(value) {
            field = value
            render()
        }

    override fun render() {
        ...
    }
}
```

Now clients setting the components state need not call `render` themselves. As shorthand, a 
[property delegate](https://kotlinlang.org/docs/reference/delegated-properties.html) is provided called `renderOnSet`. 
For example, you could rewrite `StatefulComponent` yet again:

```kotlin
class StatefulComponent : Component() {
    var state: Boolean by renderOnSet(false)

    override fun render() {
        ...
    }
}
```

## Advanced Usage

### Slotted Components

To create components that can be templated with arbitrary markup, extend the `SlottedComponent` class. This gives you 
access to the `slot` method in the markup builder DSL. The parameter to this method is the slot identifier.

For example, a simple component with two slots:

```kotlin
class Container : SlottedComponent<String>() {
    override fun render() {
        markup().div {
            div {
                slot("header")
            }
            div {
                slot("body")
            }
        }
    }
}
```

This component could be used like so:

```kotlin
override fun render() {
    markup().div {
        component(Container()) {
            slot("header") {
                h1 { +"My Header" }
            }
            slot("body") {
                p { +"My Body" }
            }
        }
    }
}
```

### ElementRef

It may be useful to have a reference the actual DOM element that Kui creates in order to run additional operations on 
the DOM. You can use an `ElementRef` class to accomplish this. Simply pass an `ElementRef` object as a `ref` to the 
element's Props, and retrieve the DOM element  the `get` method. In this example, the div element can be retrieved by 
calling `divRef.get()`:

```kotlin
class RefExample : Component() {
    val divRef = ElementRef()

    override fun render() {
        markup().div(Props(ref = divRef))
    }
}
```

**Note**: Avoid manually manipulating any properties that Kui manages on the DOM object or its children, or adding or 
removing additional elements, as this may have unexpected results if the component is re-rendered.

## Behind the Scenes

### Virtual DOM

When a component is rendered, the resulting DOM elements are not immediately created. Instead an intermediate object 
tree called the "virtual DOM" is created, which is then stored and translated into real DOM elements. Then, when the 
component is re-rendered, a new virtual DOM tree is created and compared to the previous one. Only the differences 
between the two trees gets reflected as manipulations to the actual HTML DOM; every element that did not change remains 
untouched.

This technique increases performance by reducing the number of expensive DOM manipulation operations that occur, and 
may completely skip DOM manipulation if no changes actually occured between re-renders.

### Data Binding

Kui's property-reference-based data-binding simply works by storing a reference to the `KMutableProperty` and attaching 
an event handler to the element to call the property's set method whenever a change occurs. This is why there is no 
change or input event handlers in the `Props` object as you might expect.

As such, if you wish to have custom logic apply on a change or input event, you can create a property with a custom 
setter, then pass that in as data-bound model property. Kui will then invoke your custom setter on change or input 
events.
