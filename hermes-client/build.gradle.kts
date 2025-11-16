plugins {
    `java-library`
    id("conventions.java")
    id("conventions.publication")
}

java {
    // We need to support Java 17 until all of our clients migrate to Java 21.
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    compileOnly(libs.micrometer.core)
    compileOnly(libs.jersey.client)
    compileOnly(libs.jersey.hk2)
    compileOnly(libs.spring.web)
    compileOnly(libs.spring.webflux)
    compileOnly(libs.okhttp)

    implementation(libs.failsafe)
    api(libs.reactor.core)

    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.wiremock.standalone)
    testImplementation(libs.jakarta.servlet.api)
    testImplementation(libs.jsonpath)

    testImplementation(libs.micrometer.core)
    testImplementation(libs.jersey.client)
    testImplementation(libs.jersey.hk2)
    testImplementation(libs.spring.web)
    testImplementation(libs.spring.context)
    testImplementation(libs.spring.webflux)
    testImplementation(libs.okhttp)
    testImplementation(libs.reactor.netty)
    testImplementation(libs.reactor.test)
}
