plugins {
    id("conventions.java")
}

val testArtifacts: Configuration by configurations.creating

dependencies {
    implementation(projects.hermesApi)
    implementation(projects.hermesMetrics)
    testImplementation(projects.hermesTestHelper)
    testRuntimeOnly(libs.junit.vintage.engine)
}

val testJar by tasks.registering(Jar::class) {
    archiveClassifier = "tests"
    from(sourceSets["test"].output)
}

artifacts {
    add(testArtifacts.name, testJar)
}
