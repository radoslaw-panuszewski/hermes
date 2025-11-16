package conventions

import chronicleJvmArgs
import libs
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    java
    jacoco
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withJavadocJar()
    withSourcesJar()
}

dependencies {
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

tasks {
    withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:unchecked,deprecation"))
    }

    test {
        useJUnitPlatform()
        jvmArgs = buildList {
            if (project.hasProperty("tests.timeout.multiplier")) {
                add("-Dtests.timeout.multiplier=${project.property("tests.timeout.multiplier")}")
            }
            addAll(chronicleJvmArgs)
        }
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

    javadoc {
        (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    }
}
