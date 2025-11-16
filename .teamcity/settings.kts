import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.CompoundStage
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.sequential
import jetbrains.buildServer.configs.kotlin.toId
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.version

version = "2025.07"

project {
    sequentialChain {
        parallel {
            buildType(Gradle("Unit tests", "check"))
            buildType(Gradle("Integration tests", "integrationTest"))
            buildType(Gradle("Slow integration tests", "slowIntegrationTest"))
            buildType(Gradle("JMH benchmark", "jmh"))
        }
    }
}

fun Project.sequentialChain(block: CompoundStage.() -> Unit) {
    val buildTypes = sequential(block).buildTypes()
    buildTypes.forEach(::buildType)
}

class Gradle(buildTypeName: String, tasks: String) : BuildType() {
    init {
        name = buildTypeName
        id(buildTypeName.toId())

        vcs {
            root(DslContext.settingsRoot)
        }

        steps {
            nodeJS {
                name = "Install Node"
                workingDir = "hermes-console"
                shellScript = "npm install"
            }

            gradle {
                name = "Run tests"
                this.tasks = tasks
            }
        }

        triggers {
            vcs {  }
        }
    }
}
