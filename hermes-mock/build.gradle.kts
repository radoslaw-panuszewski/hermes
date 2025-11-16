plugins {
    groovy
    java
    `java-library`
}

dependencies {
    implementation(libs.junit)
    api(libs.wiremock.standalone)
    implementation(libs.awaitility)
    api(libs.avro)
    implementation(libs.json2avro.converter)
    implementation(libs.junit.jupiter.api)

    testImplementation(project(":hermes-test-helper"))
    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.groovy.json)
    testImplementation(libs.jersey.client)
    testImplementation(libs.jersey.hk2)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.spring.test)
    testRuntimeOnly(libs.junit.vintage.engine)
}

tasks.test {
    useJUnitPlatform()
}
