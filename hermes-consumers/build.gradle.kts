plugins {
    application
    `java-library`
}

application {
    mainClass = "pl.allegro.tech.hermes.consumers.HermesConsumers"
}

val sbeClasspath: Configuration by configurations.creating

dependencies {
    implementation(project(":hermes-common"))
    api(project(":hermes-tracker"))
    implementation(project(":hermes-metrics"))
    implementation(project(":hermes-schema"))

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

    testImplementation(project(":hermes-test-helper"))
    testImplementation(libs.curator.test)
    testImplementation(libs.jakarta.servlet.api)

    testImplementation(project(":hermes-common"))

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
