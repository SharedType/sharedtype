plugins {
    `java-gradle-plugin`
    `maven-publish`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "online.sharedtype"
version = "0.14.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.sharedtype.ap)
    implementation(libs.sharedtype.ap.exec)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    val greeting by plugins.creating {
        id = "online.sharedtype.sharedtype-gradle-plugin"
        implementationClass = "online.sharedtype.gradle.SharedtypeGradlePlugin"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
