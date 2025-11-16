package conventions

plugins {
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])
            pom {
                name = project.name
                description = "Fast and reliable message broker built on top of Kafka."
                url = "https://github.com/allegro/hermes"
                inceptionYear = "2015"
                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "skyeden"
                        name = "Skylab Eden Team"
                    }
                }
                scm {
                    url = "https://github.com/allegro/hermes"
                    connection = "scm:git@github.com:allegro/hermes.git"
                    developerConnection = "scm:git@github.com:allegro/hermes.git"
                }
            }
        }
    }
}

if (System.getenv("GPG_KEY_ID") != null) {
    configure<SigningExtension> {
        useInMemoryPgpKeys(
            System.getenv("GPG_KEY_ID"),
            System.getenv("GPG_PRIVATE_KEY"),
            System.getenv("GPG_PRIVATE_KEY_PASSWORD")
        )
        sign(publishing.publications["mavenJava"])
    }
}