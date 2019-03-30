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
    compile "com.github.juggernaut0.kui:kui:0.3.0"
}
```

Creating simple components is easy:

index.html:
```html
<body>
    <div id="app"></div>
    <script src="js/kotlin.js"></script>
    <script src="js/kui.js"></script>
    <script src="js/main.js"></script>
</body>
```

Main.kt:
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

Check out the [examples](example/src/main/kotlin) for more advanced 
usage.

## Building

To publish locally, clone the project and run 
`./gradlew publishToMavenLocal`.

To run tests, first install jest with `npm install jest` or 
`./gradlew installJest`. The run the tests with `./gradlew build`.

## TODOs

* Increased test coverage
* Testing utility library for testing user applications
    * Virtual rendering, shallow renders, snapshot testing etc.
    * Sending dom events to elements
