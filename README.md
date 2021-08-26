# Kui

Pronounced 'KOO-ee' or 'KYU-ee'.

## What is it?

A Kotlin/JS library for creating component-based user interfaces for use in 
web applications. With Kui, you can create declarative, stateful, **strongly 
typed** components that can be composed to create rich web applications.

## Getting started

Add the kui dependency to your build.gradle
```groovy
repositories {
    // ...
    maven { url "https://juggernaut0.github.io/m2/repository" }
}
dependencies {
    // ...
    compile "com.github.juggernaut0.kui:kui:0.11.0"
}
```

Creating simple components is easy:

```kotlin
class HelloWorld(private val name: String) : kui.Component() {
    override fun render() {
        markup().p {
            +"Hello, $name!"
        }
    }
}

fun main() {
    kui.mountComponent(document.body!!, HelloWorld("World"))
}
```

Check out the [usage guide](/docs/usage.md), [api docs](https://juggernaut0.github.io/docs/kui/index.html) and 
[examples](example/src/main/kotlin) for more advanced usage.

## Building

To publish locally, clone the project and run 
`./gradlew publishToMavenLocal`.

To run tests, firefox headless is currently required. Once installed, run the tests with `./gradlew build`.

## TODOs

* Improve error handling & graceful failure (rollback of dom?) in case of exception during rendering
* Use a more performant vdom diffing algorithm
* Improve testing utility library for testing user applications
    * Virtual rendering
    * Shallow renders
