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

val sourceJar = tasks.register<Jar>("sourceJar") {
    from(kotlin.sourceSets.main.map { it.kotlin })
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named("jsJar"))
            artifact(sourceJar)
        }
    }

    repositories {
        maven {
            name = "pages"
            url = uri("$rootDir/pages/m2/repository")
        }
    }
}
