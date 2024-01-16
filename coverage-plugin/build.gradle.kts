// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
    idea
    `java-gradle-plugin`
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
