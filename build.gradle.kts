import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.kover.api.KoverTaskExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.dokka")

    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("org.jetbrains.kotlinx.kover") version "0.5.1"

    id("land.vani.maven.publish")
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
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("test-junit5"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")
    implementation("org.opentest4j:opentest4j:1.2.0")

    implementation("io.papermc.paper:paper-api:1.18.1-R0.1-20220104.205543-66")
    implementation("net.kyori:adventure-text-serializer-bungeecord:4.1.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.3.0")
    testImplementation("io.kotest:kotest-assertions-core:5.3.0")
    testImplementation("io.kotest:kotest-property:5.3.0")

    // Missing dependencies for LibraryLoader
    implementation("org.apache.maven:maven-aether-provider:3.3.9")
    implementation("org.eclipse.aether:aether-impl:1.1.0")
    implementation("org.eclipse.aether:aether-transport-http:1.1.0")
    implementation("org.eclipse.aether:aether-connector-basic:1.1.0")
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

    assemble {
        dependsOn("javadocJar", "sourcesJar")
    }

    withType<Test> {
        useJUnitPlatform()

        extensions.configure<KoverTaskExtension> {
            isDisabled = false
            includes = listOf("land.vani.mockpaper.*")
        }
    }

    withType<Detekt> {
        reports {
            xml.required.set(true)
        }
        jvmTarget = "16"
    }

    koverXmlReport {
        isEnabled = true
    }
}
