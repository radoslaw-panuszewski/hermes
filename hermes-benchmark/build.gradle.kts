plugins {
    alias(libs.plugins.jmh)
    id("conventions.java")
    id("conventions.buildscript-helpers")
}

jmh {
    includes = listOf("pl\\.allegro\\.tech\\.hermes\\.benchmark\\..*")
    jmhVersion = "1.36"
    zip64 = true
    verbosity = "NORMAL"
    iterations = intProperty("jmh.iterations", 4)
    timeOnIteration = stringProperty("jmh.timeOnIteration", "5s")
    fork = intProperty("jmh.fork", 1)
    warmupIterations = intProperty("jmh.warmupIterations", 4)
    warmup = stringProperty("jmh.timeOnWarmupIteration", "5s")
    jvmArgs = listProperty("jmh.jvmArgs", listOf("-Xmx1g", "-Xms1g", "-XX:+UseG1GC") + chronicleJvmArgs)
    failOnError = booleanProperty("jmh.failOnError", true)
    threads = intProperty("jmh.threads", 4)
    synchronizeIterations = false
    forceGC = false
    duplicateClassesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    jmh(libs.jmh.core)
    jmh(libs.jmh.generator.annprocess)
    jmh(libs.httpasyncclient)
    jmh(projects.hermesFrontend)
    jmh(projects.hermesTestHelper)
    jmh(projects.hermesCommon)
    jmh(projects.hermesTracker)
}

tasks.check {
    dependsOn(tasks.jmh)
}

