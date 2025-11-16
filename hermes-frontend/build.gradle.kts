plugins {
    application
    `java-library`
    id("conventions.java")
}

application {
    mainClass = "pl.allegro.tech.hermes.frontend.HermesFrontend"
}

dependencies {
    implementation(projects.hermesCommon)
    api(projects.hermesTracker)
    implementation(projects.hermesMetrics)
    implementation(projects.hermesSchema)

    api(libs.spring.boot.starter)
    api(libs.undertow.core)
    // Did not update that as we're trying to abandon buffers
    api(libs.chronicle.map) {
        exclude(group = "net.openhft", module = "chronicle-analytics")
    }
    implementation(libs.commons.io)
    implementation(libs.failsafe)

    testImplementation(projects.hermesTestHelper)

    testImplementation(libs.spock.core)
    testImplementation(libs.groovy.json)
    testImplementation(libs.awaitility.groovy)
    testImplementation(libs.awaitility)
    testImplementation(libs.testcontainers.spock)
    testImplementation(libs.testcontainers.kafka)
    testRuntimeOnly(libs.junit.vintage.engine)
}
