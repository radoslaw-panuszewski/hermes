plugins {
    id("conventions.java")
    id("conventions.buildscript-helpers")
}

val agent: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

dependencies {
    testImplementation(projects.hermesCommon)
    testImplementation(projects.hermesTestHelper)
    testImplementation(projects.hermesManagement)
    testImplementation(projects.hermesConsumers)
    testImplementation(projects.hermesFrontend)
    testImplementation(projects.hermesClient)

    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.gcloud)
    testImplementation(libs.okhttp)
    testImplementation(libs.spring.webflux)
    testImplementation(libs.spring.test)
    testImplementation(libs.jetty.reactive.httpclient)
    testImplementation(libs.awaitility.old)
    testImplementation(libs.reactive.streams)
    // TODO: can we update it ? Which version of server our clients use ?
    testImplementation(libs.hornetq.jms.server) {
        exclude(module = "hornetq-native")
    }

    // Import allure-bom to ensure correct versions of all the dependencies are used
    testImplementation(platform(libs.allure.bom))
    // Add necessary Allure dependencies to dependencies section
    testImplementation(libs.allure.junit5)

    agent(libs.aspectjweaver)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

val common = sourceSets.create("common")
configurations.named(common.implementationConfigurationName) {
    extendsFrom(configurations.testImplementation.get())
}
configurations.named(common.runtimeOnlyConfigurationName) {
    extendsFrom(configurations.testRuntimeOnly.get())
}

fun registerIntegrationTestTask(name: String, common: SourceSet) {
    val integrationTest = sourceSets.create(name)

    integrationTest.compileClasspath += common.output
    integrationTest.runtimeClasspath += common.output

    configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
    configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

    tasks.register<Test>(name) {
        testLogging.showStandardStreams = true

        jvmArgs = buildList {
            if (project.hasProperty("tests.timeout.multiplier")) {
                add("-Dtests.timeout.multiplier=${project.property("tests.timeout.multiplier")}")
            }

            if (project.hasProperty("confluentImagesTag")) {
                add("-DconfluentImagesTag=${project.property("confluentImagesTag")}")
            }

            add("-javaagent:${agent.singleFile}")
            addAll(chronicleJvmArgs)
        }

        minHeapSize = "2000m"
        maxHeapSize = "3500m"

        group = "Verification"
        description = "Runs the integration tests."
        useJUnitPlatform()

        testClassesDirs = integrationTest.output.classesDirs
        classpath = configurations[integrationTest.runtimeClasspathConfigurationName] + integrationTest.output + common.output

        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            events("passed", "skipped", "failed", "standardError", "standardOut")
        }
    }
}

registerIntegrationTestTask("integrationTest", common)
registerIntegrationTestTask("slowIntegrationTest", common)

tasks.check {
    dependsOn(tasks["integrationTest"])
    dependsOn(tasks["slowIntegrationTest"])
}
