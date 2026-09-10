plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.management)
    alias(libs.plugins.kotlin.jpa)
}

group = "dev.shockman.tenant"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mshockman/account-tenant")

            credentials {
                username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR")
                password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    implementation(project(":tenant-api"))
    implementation(libs.spring.jpa)
    implementation(libs.spring.mail)
//    implementation(libs.spring.pulsar)
    implementation(libs.spring.validation)
    implementation(libs.spring.web)
    implementation(libs.spring.kafka)
    implementation(libs.spring.actuator)

    implementation(libs.jackson.kotlin)
    implementation(libs.kotlin.reflect)
    implementation(libs.liquibase)
    implementation(libs.hypersistence.utils.hibernate)
    implementation(libs.open.telemetry)
    implementation(libs.micrometer.tracing.bridge.otel)

    implementation(libs.dev.shockman.messaging.starter)
    implementation(libs.dev.shockman.liquibase.init)
    implementation(libs.dev.shockman.logging)

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
    jvmToolchain(25)
}