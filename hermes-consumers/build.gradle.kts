plugins {
    application
    `java-library`
    id("conventions.java")
}

application {
    mainClass = "pl.allegro.tech.hermes.consumers.HermesConsumers"
}

val sbeClasspath: Configuration by configurations.creating

dependencies {
    implementation(projects.hermesCommon)
    api(projects.hermesTracker)
    implementation(projects.hermesMetrics)
    implementation(projects.hermesSchema)

    api(libs.spring.boot.starter)
    api(libs.jetty.alpn.java.client)
    api(libs.jetty.http2.client.transport)
    implementation(libs.jctools.core)
    api(libs.jakarta.jms.api)
    implementation(libs.guava.retrying) {
        exclude(module = "guava")
    }
    implementation(libs.agrona)
    // TODO: can we update it ? Which version of server our clients use ?
    implementation(libs.hornetq.jms.client) {
        exclude(module = "hornetq-native")
    }
    api(libs.google.cloud.pubsub)
    api(libs.httpcore5)
    implementation(libs.json2avro.converter)

    testImplementation(projects.hermesTestHelper)
    testImplementation(libs.curator.test)
    testImplementation(libs.jakarta.servlet.api)

    testImplementation(projects.hermesCommon)

    testImplementation(libs.awaitility.groovy)
    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testRuntimeOnly(libs.junit.vintage.engine)

    sbeClasspath(libs.sbe.all)
}

val generatedPath = "${layout.buildDirectory.get()}/generated/java/"

val generateMaxRateSbeStubs by tasks.registering(JavaExec::class) {
    description = "Generate SBE stubs for max-rate"
    classpath = sbeClasspath
    mainClass = "uk.co.real_logic.sbe.SbeTool"
    systemProperties(
        "sbe.output.dir" to generatedPath,
        "sbe.xinclude.aware" to "true"
    )
    args = listOf("src/main/resources/sbe/max-rate.xml")
}

val generateWorkloadSbeStubs by tasks.registering(JavaExec::class) {
    description = "Generate SBE stubs for workload"
    classpath = sbeClasspath
    mainClass = "uk.co.real_logic.sbe.SbeTool"
    systemProperties(
        "sbe.output.dir" to generatedPath,
        "sbe.xinclude.aware" to "true"
    )
    args = listOf("src/main/resources/sbe/workload.xml")
}

val generateSbeStubs by tasks.registering {
    description = "Generate all SBE stubs from provided schemas"
    dependsOn(generateMaxRateSbeStubs, generateWorkloadSbeStubs)
}

sourceSets {
    main {
        java.srcDir(generatedPath)
    }
}

tasks.compileJava {
    dependsOn(generateSbeStubs)
}
