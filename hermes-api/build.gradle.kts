plugins {
    groovy
    `java-library`
}

dependencies {
    api(libs.hibernate.validator)

    api(libs.jakarta.ws.rs.api)
    implementation(libs.jackson.annotations)
    api(libs.jackson.jakarta.rs.json.provider)
    api(libs.jackson.datatype.jsr310)
    implementation(libs.guava.core)
    api(libs.handy.uri.templates)
    api(libs.jakarta.xml.bind.api)

    implementation(libs.jaxb.core)
    implementation(libs.jaxb.impl)
    implementation(libs.jakarta.annotation.api)

    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(project(":hermes-test-helper"))
    testRuntimeOnly(libs.junit.vintage.engine)
}
