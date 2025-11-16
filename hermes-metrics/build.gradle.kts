plugins {
    `java-library`
}

dependencies {
    api(libs.dropwizard.metrics.core)
    api(libs.commons.text)
    api(libs.micrometer.core)

    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
}
