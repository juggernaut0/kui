plugins {
    kotlin("js") version "1.8.20"
    id("org.jetbrains.dokka") version "1.8.10"
    `maven-publish`
}

allprojects {
    group = "com.github.juggernaut0.kui"
    version = "0.15.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

kotlin {
    js(BOTH) {
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
    }
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(project(":kui-test"))
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
