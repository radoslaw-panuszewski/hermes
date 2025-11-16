plugins {
    `java-library`
    groovy
}

dependencies {
    api(project(":hermes-api"))
    api(project(":hermes-metrics"))
    api(project(":hermes-schema"))

    api(libs.curator.client) {
        exclude(module = "slf4j-log4j12")
        exclude(module = "log4j")
    }
    api(libs.curator.recipes) {
        exclude(module = "slf4j-log4j12")
        exclude(module = "log4j")
    }

    api(libs.jersey.client)
    implementation(libs.jersey.hk2)
    api(libs.jersey.media.json.jackson)
    api(libs.jersey.bean.validation)

    api(libs.json2avro.converter)

    api(libs.commons.collections4)
    implementation(libs.commons.codec)
    implementation(libs.guava.core)

    api(libs.jackson.databind)
    api(libs.avro)
    api(libs.jsonpath)

    implementation(libs.dropwizard.metrics.core)

    implementation(libs.findbugs.annotations)
    api(libs.micrometer.core)
    api(libs.micrometer.registry.prometheus)

    implementation(libs.slf4j.log4j.over.slf4j)
    implementation(libs.logback.classic)
    api(libs.kafka.clients) {
        exclude(group = "net.sf.jopt-simple")
    }

    api(libs.jakarta.inject.api)

    testImplementation(project(":hermes-test-helper"))

    testImplementation(libs.jakarta.servlet.api)

    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.awaitility.groovy)
    testRuntimeOnly(libs.junit.vintage.engine)
}
