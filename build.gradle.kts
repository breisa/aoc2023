plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "de.breisa.aoc2023"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("de.breisa.aoc2023.core.MainKt")
}