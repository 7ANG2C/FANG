import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinParcelize)
    alias(libs.plugins.compose.compiler)
}

android {
    val pkg = "com.fang.arrangement"
    namespace = pkg
    resourcePrefix = "arr_"
    defaultConfig {
        applicationId = pkg
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables.useSupportLibrary = true
        ndk { abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a")) }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    val release = "release"
    signingConfigs {
        val keystoreProperties =
            Properties().apply {
                val keystorePropertiesFile = project.file("publish/keystore.properties")
                load(FileInputStream(keystorePropertiesFile))
            }
        create(release) {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
        }
    }
    productFlavors {
        applicationVariants.all {
            val dateTime = SimpleDateFormat("MM.dd-HH.mm").format(Date())
            outputs.forEach {
                (it as? BaseVariantOutputImpl)?.outputFileName =
                    "arrangement-$versionName-$dateTime.apk"
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isDebuggable = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName(release)
        }
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1,INDEX.LIST,DEPENDENCIES}" } }
}

dependencies {
    implementation(projects.cosmos)
    implementation(libs.google.auth.oauth2)
    implementation(libs.google.apis.sheets)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.analytics)
    implementation(libs.google.firebase.crashlytics)
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