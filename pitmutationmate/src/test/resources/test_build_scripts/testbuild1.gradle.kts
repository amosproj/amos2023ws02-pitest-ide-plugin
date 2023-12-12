// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    id("java")
    id("info.solidsoft.pitest") version "1.15.0"
    id ("com.groupcdg.pitest.github") version "1.0.5"
    id("io.github.amosproj.pitmutationmate.override") version "1.0"
}

repositories {
    mavenCentral()
}

val mockitoVersion = "5.2.0"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-inline:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")

    pitest("org.junit.platform:junit-platform-launcher")
    pitest("com.groupcdg:pitest-git-plugin:1.1.2")
}

tasks.test {
    useJUnitPlatform()
}

pitest {
    mutators.set(setOf("STRONGER"))
    pitestVersion.set("1.15.2")
    //targetClasses.set(setOf("de.pfoerd.example.pitest.coffeemachine.service.CoffeeService"))
    targetTests.set(setOf("de.pfoerd.example.*Test"))
    targetClasses.set(setOf("de.pfoerd.example.*"))
    junit5PluginVersion.set("1.2.1")
    //features.add("+GIT(from[HEAD~1])")
    outputFormats.set(setOf("gitci", "HTML", "XML"))
    //features.add("+GIT(from[-EMPTY-])")
    verbose.set(false)
    //debug.set(false)
}

pitestGithub {
    deleteOldSummaries.set(true)
}
