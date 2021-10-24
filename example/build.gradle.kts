plugins {
    id("org.jetbrains.kotlin.js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}

kotlin {
    js {
        browser()
    }
}

val copyStaticWeb = tasks.register<Copy>("copyStaticWeb") {

    val someTask = tasks.getByPath("browserDevelopmentWebpack")
    someTask.outputs
    dependsOn("browserDevelopmentWebpack")
    from("static")
    from("$projectDir/build/distributions/")
    into("$projectDir/build/web")
}

tasks["assemble"].dependsOn(copyStaticWeb)
