plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.management)
    alias(libs.plugins.kotlin.jpa)
}

group = "dev.shockman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.jpa)
//    implementation(libs.spring.mail)
//    implementation(libs.spring.pulsar)
    implementation(libs.spring.validation)
    implementation(libs.spring.web)

    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.liquibase)
    implementation(libs.hypersistence.utils.hibernate)

    developmentOnly(libs.spring.devtools)
    runtimeOnly(libs.postgres)
    testImplementation(libs.spring.test)
    testImplementation(libs.kotlin.test)
    testRuntimeOnly(libs.juint.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}