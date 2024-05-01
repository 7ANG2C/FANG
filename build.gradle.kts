import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KOTLIN_OPTIONS_DSL_NAME

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinParcelize) apply false
}

subprojects {
    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(
            arrayOf("-parameters", "-Xlint:unchecked", "-Xlint:deprecation")
        )
    }

    plugins.withType<AndroidBasePlugin> {
        configure<BaseExtension> {
            compileSdkVersion(libs.versions.compileSdk.get().toInt())
            defaultConfig {
                minSdk = libs.versions.minSdk.get().toInt()
                targetSdk = libs.versions.compileSdk.get().toInt()
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }

        afterEvaluate {
            val appModuleExtension = extensions.findByType<BaseAppModuleExtension>()
            val libraryExtension = extensions.findByType<LibraryExtension>()
            val extensionAware = (appModuleExtension ?: libraryExtension) as ExtensionAware
            val kotlinOptions =
                extensionAware.extensions.getByName(KOTLIN_OPTIONS_DSL_NAME) as KotlinJvmOptions
            kotlinOptions.apply {
                jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }
    }
}