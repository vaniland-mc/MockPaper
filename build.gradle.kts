import io.gitlab.arturbosch.detekt.Detekt
import kotlinx.kover.api.KoverTaskExtension
import kotlinx.kover.api.VerificationValueType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"

    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
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

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("test-junit5"))
    implementation("org.opentest4j:opentest4j:1.2.0")

    implementation("io.papermc.paper:paper-api:1.18.1-R0.1-20220104.205543-66")
    implementation("net.kyori:adventure-text-serializer-bungeecord:4.0.1")

    testImplementation("io.kotest:kotest-runner-junit5:5.1.0")
    testImplementation("io.kotest:kotest-assertions-core:5.1.0")
    testImplementation("io.kotest:kotest-property:5.1.0")

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

    withType<Test> {
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors() / 2 + 1

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

    koverVerify {
        rule {
            bound {
                minValue = 80
                valueType = VerificationValueType.COVERED_LINES_PERCENTAGE
            }
        }
    }
}
