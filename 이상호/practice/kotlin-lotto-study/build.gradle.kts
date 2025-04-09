plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") apply false
}

java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")

group = "${property("projectGroup")}"
version = "${property("applicationVersion")}"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:${property("kotestVersion")}")
    testImplementation("io.kotest:kotest-assertions-core:${property("kotestVersion")}")
    testImplementation("io.kotest:kotest-framework-engine:${property("kotestVersion")}")
}

tasks.test {
    useJUnitPlatform()
}
