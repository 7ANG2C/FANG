import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinParcelize)
}

android {
    namespace = "com.fang.arrangement"
    resourcePrefix = "arr_"
    defaultConfig {
        applicationId = "com.fang.arrangement"
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        val keystoreProperties = Properties().apply {
            val keystorePropertiesFile = rootProject.file("publish/keystore.properties")
            load(FileInputStream(keystorePropertiesFile))
        }
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAliasArrangement")
            keyPassword = keystoreProperties.getProperty("keyPasswordArrangement")
            storeFile = file(keystoreProperties.getProperty("storeFileArrangement"))
            storePassword = keystoreProperties.getProperty("storePasswordArrangement")
        }
    }
    productFlavors {
        applicationVariants.all {
            val dateTime = SimpleDateFormat("MM.dd-HH.mm").format(Date())
            outputs.forEach {
                (it as? com.android.build.gradle.internal.api.BaseVariantOutputImpl)
                    ?.outputFileName = "arrangement-$versionName-$dateTime.apk"
            }
        }
    }

    buildTypes {
        all { isMinifyEnabled = false }
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompile.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.cosmos)
    // androidx - initial
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // androidx
    implementation(libs.androidx.navigation.compose)
    // others
    implementation(libs.retrofit.gson)
    implementation(libs.io.koin.core)
    implementation(libs.io.koin.android)
    implementation(libs.io.koin.androidx.compose)
    // debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // test
    testImplementation(libs.junit)
    // android test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}

private val dateTime get() = SimpleDateFormat("MM.dd-HH.mm").format(Date())