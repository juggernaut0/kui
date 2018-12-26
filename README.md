# Kui

Pronounced 'KOO-ee' or 'KYU-ee'.

## What is it?

A Kotlin/JS library for creating component-based user interfaces. With 
Kui, you can create declarative, stateful, **strongly typed** 
components that can be composed to create web applications.

## Example

Creating simple components is easy:

HTML:
```html
<body>
    <div id="app"></div>
</body>
```

Kotlin:
```kotlin
class HelloWorld : kui.Component() {
    override fun render() {
        markup().p {
            +"Hello, World!"
        }
    }
}

fun main() {
    kui.mountComponent("app", HelloWorld())
}
```

Check out the [examples](example\src\main\kotlin) for more advanced 
usage.

## Getting started

Maven dependency coming soon.

To publish locally, clone the project and run 
`./gradlew publishToMavenLocal`.

To run tests, first install jest with `npm install jest` or 
`.\gradlew installJest`. The run the tests with `.\gradlew build`.

## TODOs

* Expose more DOM events for elements to react to (mousenter, 
mouseleave, focus, blur, etc.)
* Increased test coverage
* Testing utility library for testing user applications
    * Virtual rendering, shallow renders, snapshot testing etc.
* JS interop layer, for use from vanilla JS or TypeScript
