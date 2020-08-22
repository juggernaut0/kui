plugins {
    kotlin("js")
    id("org.jetbrains.dokka")
    `maven-publish`
}

kotlin {
    js {
        browser()
    }
}

dependencies {
    implementation(project(":"))
    implementation("org.jetbrains.kotlin:kotlin-test-js")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named("jsJar"))
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
