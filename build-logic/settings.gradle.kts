@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("dev.panuszewski.typesafe-conventions") version "0.10.0"
}

rootProject.name = "build-logic"

include(":buildscript-helpers")
include(":java")
include(":releasing")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
