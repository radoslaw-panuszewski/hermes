import org.gradle.api.Project

fun Project.stringProperty(property: String, defaultValue: String): String =
    if (hasProperty(property)) property(property) as String else defaultValue

fun Project.listProperty(property: String, defaultValue: List<String>): List<String> =
    if (hasProperty(property)) (property(property) as String).split(" ") else defaultValue

fun Project.intProperty(property: String, defaultValue: Int): Int =
    if (hasProperty(property)) (property(property) as String).toInt() else defaultValue

fun Project.booleanProperty(property: String, defaultValue: Boolean): Boolean =
    if (hasProperty(property)) (property(property) as String).toBoolean() else defaultValue
