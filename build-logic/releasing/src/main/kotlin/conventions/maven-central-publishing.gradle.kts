package conventions

import intProperty
import libs
import java.time.Duration

plugins {
    alias(libs.plugins.nexus.publish)
}

check(project == rootProject) { "'conventions.maven-central-publishing' must be applied to root project" }

nexusPublishing {
    connectTimeout = Duration.ofMinutes(intProperty("publishingTimeoutInMin", 10).toLong())
    clientTimeout = Duration.ofMinutes(intProperty("publishingTimeoutInMin", 10).toLong())
    repositories {
        sonatype {
            nexusUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
            snapshotRepositoryUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            stagingProfileId = "19d6feb4b1fb3" // id for group 'pl.allegro.tech.hermes'
            username = System.getenv("SONATYPE_USERNAME")
            password = System.getenv("SONATYPE_PASSWORD")
        }
    }
    transitionCheckOptions {
        maxRetries = intProperty("attemptsToCloseStagingRepository", 30)
        delayBetween = Duration.ofSeconds(intProperty("delayInSecBetweenCloseStagingRepositoryAttempts", 45).toLong())
    }
}
