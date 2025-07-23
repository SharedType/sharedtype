plugins {
    `java-gradle-plugin`
    `maven-publish`
}

java {
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
    implementation("online.sharedtype:sharedtype-ap:${project.version}")
    implementation("online.sharedtype:sharedtype-ap-exec:${project.version}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
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
