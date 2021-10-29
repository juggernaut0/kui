plugins {
    kotlin("js") version "1.4.31"
    id("org.jetbrains.dokka") version "1.4.30"
    `maven-publish`
}

allprojects {
    group = "com.github.juggernaut0.kui"
    version = "0.13.0"

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

tasks.withType(Test::class.java) {
    useJUnitPlatform()
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
