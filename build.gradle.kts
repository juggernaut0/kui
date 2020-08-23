plugins {
    kotlin("js") version "1.4.0"
    id("org.jetbrains.dokka") version "0.10.1"
    `maven-publish`
}

allprojects {
    group = "com.github.juggernaut0.kui"
    version = "0.11.0"

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}

kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
    testImplementation(project(":kui-test"))
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
