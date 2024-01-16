// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    id("java", "kotlin")
    id("info.solidsoft.pitest") version "1.15.0" apply true
    //id("com.groupcdg.pitest.github") version "1.0.5"
    id("io.github.amos-pitmutationmate.pitmutationmate.override") version "1.0"
}

repositories {
    mavenLocal()
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    pitest("org.junit.platform:junit-platform-launcher")
    //pitest("com.groupcdg:pitest-git-plugin:1.1.2")
}

tasks.test {
    useJUnitPlatform()
}

pitest {
    pitestVersion.set("1.15.2")
    targetClasses.set(setOf("de.esolutions.*"))
    junit5PluginVersion.set("1.2.1")
    outputFormats.set(setOf("XML", "HTML"))
}
