import com.github.gradle.node.yarn.task.YarnTask

plugins {
    `java-library`
    application
    groovy
    alias(libs.plugins.node.gradle)
    id("conventions.java")
}

application {
    mainClass = "pl.allegro.tech.hermes.management.HermesManagement"
}

dependencies {
    api(projects.hermesApi)
    api(projects.hermesCommon)
    api(projects.hermesTracker)
    implementation(projects.hermesSchema)

    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.starter.jersey)
    implementation(libs.jopt.simple)
    implementation(libs.jersey.mvc.freemarker)

    implementation(libs.swagger.jersey2.jaxrs) {
        exclude(group = "javax.validation", module = "validation-api")
    }

    implementation(libs.kafka.clients)

    implementation(libs.commons.codec)
    implementation(libs.json.schema.validator)

    implementation(libs.commons.jxpath)
    implementation(libs.httpclient5)

    api(libs.javers.core)

    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.commons.io)

    testImplementation(projects.hermesTestHelper)
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
    val shouldExecute = tasksThatDontRequireConsole.intersect(gradle.startParameter.taskNames.toSet()).isEmpty()
    onlyIf { shouldExecute }

    args = listOf("build-only")
}

tasks.named("yarn") {
    finalizedBy(buildHermesConsole)
}

val attachHermesConsole by tasks.registering(Copy::class) {
    dependsOn(buildHermesConsole)
    from("../hermes-console/dist")
    into(layout.buildDirectory.dir("resources/main/static"))
}

val prepareIndexTemplate by tasks.registering(Copy::class) {
    val resourcesDir = tasks.processResources.map { it.destinationDir }
    from(resourcesDir.map { it.resolve("resources/main/static/index.html") })
    into(resourcesDir.map { it.resolve("resources/main/static/index.ftl") })
}

tasks.named("compileTestGroovy") {
    dependsOn(attachHermesConsole, prepareIndexTemplate)
}

tasks.named("javadoc") {
    dependsOn(attachHermesConsole, prepareIndexTemplate)
}

tasks.jar {
    dependsOn(attachHermesConsole, prepareIndexTemplate)
}

tasks.named("run") {
    dependsOn(attachHermesConsole, prepareIndexTemplate)
}

