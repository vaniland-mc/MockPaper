import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"

    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

group = "land.vani.mockpaper"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        name = "PaperMC"
        url = uri("https://papermc.io/repo/repository/maven-public/")
        content {
            includeGroup("io.papermc.paper")
        }
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.19.0")

    implementation(kotlin("stdlib"))
    implementation(kotlin("test-junit5"))
    implementation("org.opentest4j:opentest4j:1.2.0")

    implementation("io.papermc.paper:paper-api:1.17.1-R0.1-20211219.175449-302")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "$targetJavaVersion"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<Detekt> {
        reports {
            xml.required.set(true)
        }
        jvmTarget = "16"
    }
}
