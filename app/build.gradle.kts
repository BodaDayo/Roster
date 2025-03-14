import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.androidx.compose.compiler)
}

android {
    namespace = "com.rgbstudios.roster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rgbstudios.roster"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Load properties from local.properties file
        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties().apply {
            if (localPropertiesFile.exists()) {
                load(localPropertiesFile.inputStream())
            }
        }

        // Read the properties from local.properties
        val supabaseUrl = localProperties.getProperty("SUPABASE_URL")
        val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY")

        // Add BuildConfig fields if they exist
        if (supabaseUrl != null && supabaseAnonKey != null) {
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        } else {
            // Optionally handle missing properties
            throw GradleException("Missing SUPABASE_URL or SUPABASE_ANON_KEY in local.properties")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    //Coil
    implementation(libs.coil)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.postgrest)

    // Ktor
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.plugins)
    implementation(libs.ktor.client.core)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // MMKV
    implementation(libs.mmkv)

    // Datastore
    implementation (libs.androidx.datastore.preferences)

    // ImageCrop
    implementation (libs.imagecropview)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
