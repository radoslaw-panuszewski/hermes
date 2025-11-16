import com.github.gradle.node.yarn.task.YarnTask

plugins {
    `java-library`
    application
    alias(libs.plugins.node.gradle)
}

application {
    mainClass = "pl.allegro.tech.hermes.management.HermesManagement"
}

dependencies {
    api(project(":hermes-api"))
    api(project(":hermes-common"))
    api(project(":hermes-tracker"))
    implementation(project(":hermes-schema"))

    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.starter.jersey)
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")
    implementation(libs.jersey.mvc.freemarker)

    implementation("io.swagger:swagger-jersey2-jaxrs:1.6.14") {
        exclude(group = "javax.validation", module = "validation-api")
    }

    implementation(libs.kafka.clients)

    implementation("commons-codec:commons-codec:1.16.1")
    implementation("com.github.java-json-tools:json-schema-validator:2.2.14")

    implementation("commons-jxpath:commons-jxpath:1.3")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

    api("org.javers:javers-core:7.4.2")

    implementation(libs.jackson.datatype.jsr310)
    implementation("commons-io:commons-io:2.16.1")

    testImplementation(project(":hermes-test-helper"))
    testImplementation(libs.spring.boot.starter.test)

    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.spock.spring)
    testImplementation(libs.groovy.json)

    testImplementation(libs.testcontainers.spock)
    testImplementation(libs.testcontainers.kafka)
}

node {
    version = "20.4.0"
    distBaseUrl = null
    download = true
    workDir = file("${layout.buildDirectory.get()}/nodejs")
    npmWorkDir = file("${layout.buildDirectory.get()}/npm")
    nodeProjectDir = file("${project.rootDir}/hermes-console")
}

tasks.named("yarnSetup") {
    dependsOn(tasks.named("nodeSetup"))
}

tasks.named("yarn") {
    dependsOn(tasks.named("npmSetup"))
}

val buildHermesConsole by tasks.registering(YarnTask::class) {
    val tasksThatDontRequireConsole = listOf(
        "integrationTest",
        "slowIntegrationTest",
        "check"
    )

    onlyIf {
        tasksThatDontRequireConsole.intersect(gradle.startParameter.taskNames.toSet()).isEmpty()
    }

    args = listOf("build-only")
}

tasks.named("yarn") {
    finalizedBy(buildHermesConsole)
}

val attachHermesConsole by tasks.registering(Copy::class) {
    dependsOn(buildHermesConsole)
    from("../hermes-console/dist")
    val staticDirectory = "${sourceSets.main.get().output.resourcesDir!!.path}/static"
    // remove previous static dir if exists and start with clear setup
    doFirst {
        delete(staticDirectory)
    }
    into(staticDirectory)
}

val prepareIndexTemplate by tasks.registering {
    doLast {
        val indexPath = "${sourceSets.main.get().output.resourcesDir!!.path}/static/index.html"
        ant.withGroovyBuilder {
            "copy"("file" to indexPath, "tofile" to "$indexPath.ftl")
        }
    }
}

tasks.named("compileTestGroovy") {
    dependsOn(attachHermesConsole)
}

tasks.named("javadoc") {
    dependsOn(attachHermesConsole)
}

tasks.jar {
    dependsOn(attachHermesConsole, prepareIndexTemplate)
}

tasks.named("run") {
    dependsOn(attachHermesConsole, prepareIndexTemplate)
}

