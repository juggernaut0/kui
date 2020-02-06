# Testing Usage

The `kui-test` library contains helpful utilities for testing your Kui UI.

## Project Setup

Add the kui-test dependency to your build.gradle
```groovy
repositories {
    // ...
    maven { url "https://juggernaut0.github.io/m2/repository" }
}
dependencies {
    // ...
    compile "com.github.juggernaut0.kui:kui:0.10.0"
    testCompile "com.github.juggernaut0.kui:kui-test:0.10.0"
}
```

## Rendering for Test

The best way to test components is to actually render them as DOM objects and manipulate their behavior as a user would. 
Use the `render` method to render your component to a temporary element and returns a `RenderedComponent` object that 
can be used to interact with the component.

```kotlin
import kui.test.*

class MyCompTest {
    @Test
    fun myCompTest() {
        val rendered = render(MyComponent())
    }
}
```

Use the `getBy...` methods on the `RenderedComponent` to select specific DOM elements, to inspect them or dispatch 
events. Use the `setState` method to interact with the component's state and re-render it.

## Asserting HTML

Use the `assertMatchesHtml` assertion function to compare your component's rendered elements to an expected HTML string.

```kotlin
@Test
fun myCompTest() {
    val rendered = render(MyComponent())
    assertMatchesHtml("<div><p>Hello World</p></div>", rendered)
}
``` 

**Note**: `assertMatchesHtml` does a string comparison on the rendered component's innerHtml, so order of attributes, 
classes, etc. are important!

If you wish to use Kui's markup DSL for HTML comparison's, you can use the `assertMatchesMarkup` assertion 
function.

```kotlin
@Test
fun myCompTest() {
    val rendered = render(MyComponent())
    assertMatchesMarkup(rendered) {
        it.div {
            p { +"Hello World" }
        }
    }
}
```
