import java.time.Duration
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.publish.maven.MavenPublication
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionCreator
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionCreator.VERSION_WITH_BRANCH

plugins {
    alias(libs.plugins.axion.release)
    alias(libs.plugins.nexus.publish)
    id("conventions.buildscript-helpers")
}

scmVersion {
    tag {
        prefix = "hermes-"
    }
    versionCreator = VERSION_WITH_BRANCH.versionCreator
}

allprojects {
    group = "pl.allegro.tech.hermes"
    version = rootProject.scmVersion.version
}

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

dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("warn")
            }
        }
    }
}
