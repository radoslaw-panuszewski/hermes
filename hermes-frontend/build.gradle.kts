plugins {
    application
    `java-library`
}

application {
    mainClass = "pl.allegro.tech.hermes.frontend.HermesFrontend"
}

dependencies {
    implementation(project(":hermes-common"))
    api(project(":hermes-tracker"))
    implementation(project(":hermes-metrics"))
    implementation(project(":hermes-schema"))

    api(libs.spring.boot.starter)
    api(libs.undertow.core)
    // Did not update that as we're trying to abandon buffers
    api(libs.chronicle.map) {
        exclude(group = "net.openhft", module = "chronicle-analytics")
    }
    implementation(libs.commons.io)
    implementation(libs.failsafe)

    testImplementation(project(":hermes-test-helper"))

    testImplementation(libs.spock.core)
    testImplementation(libs.groovy.json)
    testImplementation(libs.awaitility.groovy)
    testImplementation(libs.awaitility)
    testImplementation(libs.testcontainers.spock)
    testImplementation(libs.testcontainers.kafka)
    testRuntimeOnly(libs.junit.vintage.engine)
}
