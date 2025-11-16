val chronicleMapJvmArgs: List<*> by rootProject.extra

val agent: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

dependencies {
    testImplementation(project(":hermes-common"))
    testImplementation(project(":hermes-test-helper"))
    testImplementation(project(":hermes-management"))
    testImplementation(project(":hermes-consumers"))
    testImplementation(project(":hermes-frontend"))
    testImplementation(project(":hermes-client"))

    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.gcloud)
    testImplementation(libs.okhttp)
    testImplementation(libs.spring.webflux)
    testImplementation(libs.spring.test)
    testImplementation("org.eclipse.jetty:jetty-reactive-httpclient:4.0.3")
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("org.reactivestreams:reactive-streams:1.0.4")
    // TODO: can we update it ? Which version of server our clients use ?
    testImplementation("org.hornetq:hornetq-jms-server:2.4.1.Final") {
        exclude(module = "hornetq-native")
    }

    // Import allure-bom to ensure correct versions of all the dependencies are used
    testImplementation(platform(libs.allure.bom))
    // Add necessary Allure dependencies to dependencies section
    testImplementation(libs.allure.junit5)

    agent("org.aspectj:aspectjweaver:1.9.21")

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

        val args = mutableListOf<String>()
        if (project.hasProperty("tests.timeout.multiplier")) {
            args += "-Dtests.timeout.multiplier=${project.property("tests.timeout.multiplier")}"
        }

        if (project.hasProperty("confluentImagesTag")) {
            args += "-DconfluentImagesTag=${project.property("confluentImagesTag")}"
        }

        args += "-javaagent:${agent.singleFile}"

        @Suppress("UNCHECKED_CAST")
        args += chronicleMapJvmArgs as List<String>

        jvmArgs = args
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
