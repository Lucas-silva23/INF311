plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.maps_lucas_alves"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.maps_lucas_alves"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Google Maps SDK
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // (Opcional) Localização do usuário
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Bibliotecas comuns
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
