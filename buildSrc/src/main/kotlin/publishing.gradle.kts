plugins {
    `maven-publish`
    signing
}

val publishing = extensions.getByName<PublishingExtension>("publishing").apply {
    publications {
        create<MavenPublication>("ossrh") {
            artifact(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            groupId = "land.vani.mockpaper"
            artifactId = project.name
            version = property("mockpaper.version").toString()
            pom {
                name.set(project.name)
                description.set(
                    "MockPaper is a framework that makes the unit testing of Paper/Spigot/Bukkit plugins a lot easier"
                )
                url.set("https://github.com/vaniland-mc/MockPaper")
                licenses {
                    license {
                        name.set("GPL v3")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("rona_tombo")
                        name.set("rona_tombo")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/vaniland-mc/MockPaper.git")
                    developerConnection.set("scm:git:ssh://github.com:vaniland-mc/MockPaper.git")
                    url.set("https://github.com/vaniland-mc/MockPaper")
                }
            }

            repositories {
                maven {
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                    name = "OSSRH Staging"
                    credentials {
                        username = System.getenv("OSSRH_USERNAME")
                        password = System.getenv("OSSRH_PASSWORD")
                    }
                }
            }
        }
    }
}

extensions.configure<SigningExtension>("signing") {
    useInMemoryPgpKeys(
        System.getenv("SIGNING_KEY_ID"),
        System.getenv("SIGNING_SECRET_KEY"),
        System.getenv("SIGNING_PASSWORD"),
    )
    sign(publishing.publications["ossrh"])
}
