plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    //id("com.vanniktech.maven.publish") version "0.34.0" //currently only used for local publish, plugin is published bto Gradle Plugin Portal
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "online.sharedtype"

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
    website = "https://github.com/SharedType/sharedtype"
    vcsUrl = "https://github.com/SharedType/sharedtype.git"
    plugins {
        create("sharedtype") {
            id = "online.sharedtype.sharedtype-gradle-plugin"
            displayName = "SharedType Gradle Plugin"
            description = "SharedType Gradle Plugin, SharedType is the tool to generate types from Java to target languages."
            tags = listOf("sharedtype", "java", "annotation processing", "type generation")
            implementationClass = "online.sharedtype.gradle.SharedtypeGradlePlugin"
        }
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
//
//mavenPublishing {
//    pom {
//        name.set("SharedType Gradle Plugin")
//        description.set("SharedType Gradle Plugin, SharedType is the tool to generate types from Java to target languages.")
//        inceptionYear.set("2024")
//        url.set("https://github.com/SharedType/sharedtype")
//        licenses {
//            license {
//                name.set("CC BY 4.0")
//                url.set("https://creativecommons.org/licenses/by/4.0/deed.en")
//                distribution.set("https://creativecommons.org/licenses/by/4.0/deed.en")
//            }
//        }
//        developers {
//            developer {
//                id.set("cuzfrog")
//                name.set("Cause Chung")
//                url.set("https://github.com/cuzfrog")
//            }
//        }
//        scm {
//            url.set("https://github.com/SharedType/sharedtype")
//            connection.set("scm:git@github.com:SharedType/sharedtype.git")
//        }
//    }
//}
