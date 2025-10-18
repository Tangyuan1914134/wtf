plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.example.timetableapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.timetableapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.navigation:navigation-compose:2.8.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Accompanist system UI controller (optional)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    // Testing (optional minimal)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.02"))
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
