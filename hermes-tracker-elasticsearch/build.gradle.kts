plugins {
    `java-library`
}

dependencies {
    implementation(project(":hermes-common"))
    implementation(project(":hermes-tracker"))
    implementation(libs.slf4j.log4j.over.slf4j)
    api(libs.elasticsearch.transport)

    testImplementation(project(":hermes-tracker", configuration = "testArtifacts"))
    testImplementation(project(":hermes-test-helper"))
    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.testcontainers.elasticsearch)
    testRuntimeOnly(libs.junit.vintage.engine)
}
