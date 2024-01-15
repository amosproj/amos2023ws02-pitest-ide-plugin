// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`

    // Apply the Groovy plugin to add support for Groovy
    groovy
    idea
    `maven-publish`

    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("commons-beanutils:commons-beanutils-core:1.8.3")
    implementation("com.netflix.nebula:nebula-test:10.3.0")

    testImplementation("org.spockframework:spock-core:2.2-groovy-3.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.9.2")
}

group = "io.github.amosproj"
version = "1.0"

gradlePlugin {
    website = "https://github.com/amosproj/amos2023ws02-pitest-ide-plugin"
    vcsUrl = "https://github.com/amosproj/amos2023ws02-pitest-ide-plugin.git"

    plugins {
        create("pitmutationmate-partner-plugin") {
            id = "io.github.amosproj.pitmutationmate.override"
            displayName = "PITMutationMate Partner Plugin"
            description = "A plugin that lets you override the PITest settings of the gradle-pitest-plugin " +
                "to use with the PITMutationMate  Intellij plugin."
            tags =
                listOf(
                    "pitmutationmate", "pitest", "mutation", "mutation testing", "mutation testing tool",
                    "mutation testing framework", "mutation testing gradle plugin", "mutation testing ide plugin",
                    "mutation testing intellij plugin", "mutation testing intellij idea plugin"
                )
            implementationClass = "io.github.amosproj.pitmutationmate.override.PITSettingOverridePlugin"
        }
    }
}

// Add a source set for the functional test suite
val functionalTestSourceSet =
    sourceSets.create("functionalTest") {
    }

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

tasks.named<Task>("check") {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    // Use JUnit Jupiter for unit tests.
    useJUnitPlatform()
}
