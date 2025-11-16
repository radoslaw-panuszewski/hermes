plugins {
    id("conventions.java")
}

dependencies {
    implementation(projects.hermesApi)

    implementation(libs.avro)
    implementation(libs.guava.core)

    testImplementation(projects.hermesTestHelper)

    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
}
