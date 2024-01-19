// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
    signing
}

group = "io.github.amosproj"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.pitest:pitest-command-line:1.15.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            groupId = group.toString()
            artifactId = "coverage-reporter"
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set("Coverage Reporter")
                description.set("Coverage Report writer for PIT")
                url.set("https://github.com/amosproj/amos2023ws02-pitest-ide-plugin")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }

                developers {
                    developer {
                        id.set("pitmutationmate")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/amosproj/amos2023ws02-pitest-ide-plugin.git")
                    developerConnection.set("scm:git:ssh://github.com/amosproj/amos2023ws02-pitest-ide-plugin.git")
                    url.set("https://github.com/amosproj/amos2023ws02-pitest-ide-plugin")
                }
            }

        }
    }

    repositories {
        maven {
            name = "OSSRH"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = project.findProperty("ossrhUsername").toString()
                password = project.findProperty("ossrhPassword").toString()
            }
        }

    }
}

val signingKey: String? by project
val signingPassword: String? by project

signing {
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}
