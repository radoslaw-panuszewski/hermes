package conventions

import libs
import pl.allegro.tech.build.axion.release.domain.PredefinedVersionCreator.VERSION_WITH_BRANCH

plugins {
    alias(libs.plugins.axion.release)
}

check(project == rootProject) { "'conventions.versioning' must be applied to root project" }

scmVersion {
    tag {
        prefix = "hermes-"
    }
    versionCreator = VERSION_WITH_BRANCH.versionCreator
}

allprojects {
    group = "pl.allegro.tech.hermes"
    version = rootProject.scmVersion.version
}
