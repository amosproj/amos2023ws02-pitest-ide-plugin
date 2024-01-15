// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
    idea
    `java-gradle-plugin`
    id("signing")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "coverage-reporter"
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set("Coverage Reporter")
                description.set("Coverage Report writer for PIT")
            }
        }
    }

    repositories {
        maven {
            credentials {
                username = project.findProperty("spaceUsername").toString()
                password = project.findProperty("spacePassword").toString()
            }
            url = uri("https://maven.pkg.jetbrains.space/pitmutationmate/p/main/coverage-reporter")
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
