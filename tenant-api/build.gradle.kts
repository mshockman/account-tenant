plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "dev.shockman.tenant"

val raw = providers.gradleProperty("releaseVersion").orNull
version = raw?.removePrefix("api/v") ?: "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.dev.shockman.messaging)
    implementation(libs.jackson.kotlin)
//    implementation(libs.jackson.databind)
}

kotlin {
    jvmToolchain(25)
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mshockman/tenant-service")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                    ?: findProperty("gpr.user") as String?
                password = System.getenv("GITHUB_TOKEN")
                    ?: findProperty("gpr.key") as String?
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            // groupId/artifactId/version default from project unless overridden
            // artifactId = project.name
        }
    }
}