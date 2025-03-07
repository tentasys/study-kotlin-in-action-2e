rootProject.name = "kotlin-lotto-study"

pluginManagement {
    val kotlinVersion: String by settings
    val ktlintVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "org.jlleitschuh.gradle.ktlint" -> useVersion(ktlintVersion)
            }
        }
    }
}
