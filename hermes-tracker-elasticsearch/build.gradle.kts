plugins {
    `java-library`
    id("conventions.java")
}

dependencies {
    implementation(projects.hermesCommon)
    implementation(projects.hermesTracker)
    implementation(libs.slf4j.log4j.over.slf4j)
    api(libs.elasticsearch.transport)

    testImplementation(project(":hermes-tracker", configuration = "testArtifacts"))
    testImplementation(projects.hermesTestHelper)
    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.testcontainers.elasticsearch)
    testRuntimeOnly(libs.junit.vintage.engine)
}
