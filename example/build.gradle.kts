plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
}
