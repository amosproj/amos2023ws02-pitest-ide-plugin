// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    kotlin("jvm") version "1.9.21"
    idea
}

group = "io.github.amosproj"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // https://mvnrepository.com/artifact/org.pitest/pitest-command-line
    implementation("org.pitest:pitest-command-line:1.15.3")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        // Define your Kotlin source directory for the extension
        kotlin.srcDirs("src/main/kotlin")
        resources.srcDirs("src/main/resources") // Include resources explicitly
    }
}

tasks.jar {
    dependsOn("compileKotlin") // Ensure compilation is done before JAR creation

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "your.package.MainClass" // Specify your main class
        // Add other attributes if needed
    }

    from(sourceSets.main.get().output)

    // Include dependencies
    val runtimeClasspath = configurations.runtimeClasspath.get()
    from({ runtimeClasspath.map { file -> zipTree(file) } })
}
