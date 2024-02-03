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

val beanUtilsVersion = "1.8.3"
val nebulaTestVersion = "10.3.0"
val spockVersion = "2.2-groovy-3.0"
val junitPlatformVersion = "1.9.2"

dependencies {
    implementation("commons-beanutils:commons-beanutils-core:$beanUtilsVersion")

    testImplementation("com.netflix.nebula:nebula-test:$nebulaTestVersion")
    testImplementation("org.spockframework:spock-core:$spockVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
}

group = "io.github.amos-pitmutationmate.pitmutationmate.override"
version = "1.3"

gradlePlugin {
    website = "https://github.com/amosproj/amos2023ws02-pitest-ide-plugin"
    vcsUrl = "https://github.com/amosproj/amos2023ws02-pitest-ide-plugin.git"

    plugins {
        create("pitmutationmate-partner-plugin") {
            id = "io.github.amos-pitmutationmate.pitmutationmate.override"
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
