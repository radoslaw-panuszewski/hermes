plugins {
    `java-library`
    id("conventions.java")
    id("conventions.publication")
}

dependencies {
    implementation(projects.hermesApi)
    implementation(projects.hermesCommon)
    implementation(projects.hermesConsumers)

    implementation(libs.jersey.client)
    implementation(libs.jersey.hk2)
    implementation(libs.jersey.proxy.client)
    api(libs.commons.io)
    api(libs.wiremock.standalone)
    api(libs.curator.test) {
        exclude(module = "slf4j-log4j12")
        exclude(module = "log4j")
    }
    implementation(libs.curator.client) {
        exclude(module = "slf4j-log4j12")
        exclude(module = "log4j")
    }
    implementation(libs.curator.recipes) {
        exclude(module = "slf4j-log4j12")
        exclude(module = "log4j")
    }
    implementation(libs.spotbugs.annotations)
    implementation(libs.awaitility.groovy)
    implementation(libs.assertj.core)
    api(libs.json.unit.fluent)
    implementation(libs.junit.jupiter.api)
    implementation(libs.httpclient5)
    implementation(libs.jsonpath)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.spring.test)
    implementation(libs.spring.webflux)
    implementation(libs.awaitility.old)
    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)

    implementation(libs.testcontainers)
    implementation(libs.testcontainers.toxiproxy)
    implementation(libs.testcontainers.gcloud)
}
