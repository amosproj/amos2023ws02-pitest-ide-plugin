// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.0"
  id("org.jetbrains.intellij") version "1.15.0"
  id("info.solidsoft.pitest") version "1.15.0"
}

tasks.test {
  useJUnitPlatform()
}

dependencies {

  // https://mvnrepository.com/artifact/org.apache.maven.shared/maven-invoker
  implementation("org.apache.maven.shared:maven-invoker:3.2.0")

  implementation("org.gradle:gradle-tooling-api:7.3-20210825160000+0000")
  // The tooling API need an SLF4J implementation available at runtime, replace this with any other implementation
  runtimeOnly("org.slf4j:slf4j-simple:1.7.10")

  implementation("org.pitest:pitest-command-line:1.7.0")
  implementation("org.junit.jupiter:junit-jupiter:5.8.1")
  implementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
  // https://mvnrepository.com/artifact/org.jfree/jfreechart
  implementation("org.jfree:jfreechart:1.0.19")
  // https://mvnrepository.com/artifact/jfree/jcommon
  implementation("org.jfree:jcommon:1.0.24")
}

group = "com.amos.pitmutationmate"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven {
    url = uri("https://mvnrepository.com/artifact/org.apache.maven/maven-plugin-api")
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2022.2.5")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf(/* Plugin Dependencies */))
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
    sinceBuild.set("222")
    untilBuild.set("232.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
