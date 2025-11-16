@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

        mavenCentral()
        maven { url = uri("https://repository.jboss.org/nexus/content/groups/public") }

        // https://github.com/node-gradle/gradle-node-plugin/blob/main/docs/faq.md#is-this-plugin-compatible-with-centralized-repositories-declaration
        ivy {
            name = "Node.js"
            setUrl("https://nodejs.org/dist/")
            patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
            metadataSources { artifact() }
            content { includeModule("org.nodejs", "node") }
        }
    }
}

rootProject.name = "hermes"

include(
    "hermes-common",
    "hermes-frontend",
    "hermes-management",
    "hermes-consumers",
    "hermes-api",
    "hermes-client",
    "hermes-test-helper",
    "hermes-tracker",
    "hermes-metrics",
    "hermes-tracker-elasticsearch",
    "hermes-schema",
    "hermes-benchmark",
    "hermes-mock",
    "integration-tests"
)

includeBuild("build-logic")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
