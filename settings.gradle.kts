pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "IrisApp"
include(":app")

// Secrets locales: local.properties + keystore.properties (no commitear)
gradle.beforeProject {
    listOf("local.properties", "keystore.properties").forEach { fileName ->
        val file = rootProject.file(fileName)
        if (!file.exists()) return@forEach
        val props = java.util.Properties()
        file.inputStream().use { props.load(it) }
        props.forEach { key, value ->
            val k = key.toString()
            if (k != "sdk.dir") extensions.extraProperties.set(k, value.toString())
        }
    }
}