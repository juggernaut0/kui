plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
    `maven-publish`
}

kotlin {
    js(BOTH) {
        browser()
    }
}

dependencies {
    implementation(project(":"))
    implementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named("jsIrJar"))
            artifact(tasks.named("jsLegacyJar"))
            artifact(tasks.named("kotlinSourcesJar"))
        }
    }

    repositories {
        maven {
            name = "pages"
            url = uri("$rootDir/pages/m2/repository")
        }
    }
}
