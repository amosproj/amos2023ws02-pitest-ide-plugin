// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    `maven-publish`
    idea

    id("com.gradle.plugin-publish") version "1.2.1"
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
kotlin {
    jvmToolchain(17)
}

gradlePlugin {
    website = "https://github.com/amosproj/amos2023ws02-pitest-ide-plugin"
    vcsUrl = "https://github.com/amosproj/amos2023ws02-pitest-ide-plugin.git"

    plugins {
        create("pitmutationmate-coverage-plugin") {
            id = "io.github.amosproj.pitmutationmate.coverage"
            displayName = "PITMutationMate Coverage Pitest Plugin"
            description = "A plugin that implements the MutationResultListener class from pitest " +
                    "The custom MutationResultListener provides the coverage information in a handy way " +
                    "for the PITMutationMate  Intellij plugin."
            tags =
                listOf(
                    "pitmutationmate", "pitest", "mutation", "mutation testing", "mutation testing tool",
                    "mutation testing framework", "mutation testing gradle plugin", "mutation testing ide plugin",
                    "mutation testing intellij plugin", "mutation testing intellij idea plugin"
                )
            implementationClass = "io.github.amosproj.pitmutationmate.coverage.CoverageResultListenerFactory"
        }
    }
}
