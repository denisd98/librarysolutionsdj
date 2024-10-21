plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.librarysolutionsdj"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.librarysolutionsdj"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // JUnit para pruebas unitarias
    testImplementation("junit:junit:4.13.2")

    // Mockito para pruebas unitarias y de integración
    testImplementation("org.mockito:mockito-core:4.11.0")
    androidTestImplementation("org.mockito:mockito-android:4.11.0")

    // Espresso para pruebas de UI
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // JUnit para pruebas instrumentadas de Android
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}