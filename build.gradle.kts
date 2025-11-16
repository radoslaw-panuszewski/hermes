import java.time.Duration
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.publish.maven.MavenPublication

plugins {
    java
    `maven-publish`
    alias(libs.plugins.axion.release)
    alias(libs.plugins.nexus.publish)
}

scmVersion {
    tag {
        prefix = "hermes-"
    }
    versionCreator("versionWithBranch")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

nexusPublishing {
    connectTimeout = Duration.ofMinutes(getIntProperty("publishingTimeoutInMin", 10).toLong())
    clientTimeout = Duration.ofMinutes(getIntProperty("publishingTimeoutInMin", 10).toLong())
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            stagingProfileId = "19d6feb4b1fb3" // id for group 'pl.allegro.tech.hermes'
            username = System.getenv("SONATYPE_USERNAME")
            password = System.getenv("SONATYPE_PASSWORD")
        }
    }
    transitionCheckOptions {
        maxRetries.set(getIntProperty("attemptsToCloseStagingRepository", 30))
        delayBetween.set(Duration.ofSeconds(getIntProperty("delayInSecBetweenCloseStagingRepositoryAttempts", 45).toLong()))
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "groovy")

    group = "pl.allegro.tech.hermes"
    version = rootProject.scmVersion.version

    // https://chronicle.software/chronicle-support-java-17/
    val chronicleMapJvmArgs = listOf(
        "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED"
    )
    
    extra["chronicleMapJvmArgs"] = chronicleMapJvmArgs

    dependencies {
        val libs = rootProject.libs

        implementation(libs.slf4j.api)
        implementation(libs.commons.lang3)

        // Allure Spock adapter
        testImplementation(platform(libs.allure.bom))
        testImplementation(libs.allure.spock2)
        testImplementation(libs.allure.junit.platform)

        // Spock framework
        testImplementation(platform(libs.spock.bom))
        testImplementation(libs.spock.core)

        testImplementation(libs.junit)
        testImplementation(libs.junit.dataprovider)
        testImplementation(libs.mockito.core)
        testImplementation(libs.assertj.core)
        testImplementation(libs.awaitility)

        annotationProcessor(libs.spring.boot.configuration.processor)
    }

    tasks.test {
        useJUnitPlatform()
        val args = mutableListOf<String>()
        if (project.hasProperty("tests.timeout.multiplier")) {
            args.add("-Dtests.timeout.multiplier=${project.property("tests.timeout.multiplier")}")
        }
        args.addAll(chronicleMapJvmArgs)
        jvmArgs = args
    }
}


configure(subprojects - project(":integration-tests")) {
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks.javadoc {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
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
}

subprojects {
    val libs = rootProject.libs

    configurations.all {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "log4j", module = "log4j")
        resolutionStrategy {
            force(libs.guava.core.get().toString())
            force(libs.jackson.databind.get().toString())
            force(libs.jackson.annotations.get().toString())
            force(libs.jackson.jaxrs.json.provider.get().toString())
        }
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:unchecked,deprecation"))
    }

    tasks.test {
        reports {
            html.required = false
            junitXml.required = true
            junitXml.outputLocation = file("${layout.buildDirectory.get()}/test-results/$name")
        }

        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}

fun getIntProperty(name: String, defaultValue: Int): Int {
    return (project.findProperty(name) as? String)?.toIntOrNull() ?: defaultValue
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
