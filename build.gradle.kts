import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinParcelize) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt)
}

allprojects {
    // setup spotless
    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint().setEditorConfigPath("${rootProject.rootDir}/.editorconfig")
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
    }

    // setup detekt
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config.from("${rootProject.rootDir}/config/detekt.yml")
        baseline = file("$projectDir/config/baseline.xml")
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(
            arrayOf("-parameters", "-Xlint:unchecked", "-Xlint:deprecation"),
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
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
            with(buildFeatures) {
                buildConfig = true
            }
        }

        afterEvaluate {
            with(extensions.getByType<KotlinAndroidProjectExtension>()) {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }
        }
    }
}
