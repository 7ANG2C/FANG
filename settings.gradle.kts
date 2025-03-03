rootProject.name = "FangMono"

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
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id in setOf("com.android.library", "com.android.application")) {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
            if (requested.id.id in setOf("org.jetbrains.kotlin.android", "kotlin-parcelize", "org.jetbrains.kotlin.kapt")) {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
            if (requested.id.id in setOf("com.diffplug.spotless")) {
                useModule("com.diffplug.spotless:spotless-plugin-gradle:${requested.version}")
            }
            if (requested.id.id in setOf("io.gitlab.arturbosch.detekt")) {
                useModule("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${requested.version}")
            }
        }
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// include(":free")
include(":arrangement")
include(":cosmos")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
