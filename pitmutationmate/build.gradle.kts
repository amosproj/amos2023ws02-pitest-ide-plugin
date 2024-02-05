// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.17.0"
    kotlin("plugin.serialization") version "1.9.22"
}

tasks.test {
    useJUnitPlatform()
}

val gradleToolingApiVersion = "7.4.2"
val pitestCommandLineVersion = "1.7.0"
val junitVersion = "5.8.1"
val mockitoVersion = "5.8.0"
val slf4jVersion = "2.0.11"
val kotlinSerializationVersion = "1.6.0"

dependencies {
    implementation("org.pitest:pitest-command-line:$pitestCommandLineVersion")
    implementation("org.gradle:gradle-tooling-api:$gradleToolingApiVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

    runtimeOnly("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

group = "com.amos.pitmutationmate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.gradle.org/gradle/libs-releases/")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("org.jetbrains.kotlin", "com.intellij.java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    register<org.jetbrains.intellij.tasks.RunIdeTask>("runIdeAndroidStudio") {
        val androidStudioHome = findProperty("androidStudioHome")
        if (androidStudioHome != null) {
            ideDir.set(file(androidStudioHome))
        }
    }
}
