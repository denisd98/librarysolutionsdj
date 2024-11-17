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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(files("libs/Full_Model.jar"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.espresso.idling.resource)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    testImplementation(libs.core)
    testImplementation(libs.ext.junit)
    testImplementation(libs.espresso.intents)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.tracing:tracing:1.1.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") // Añadir esta línea para Espresso Intents
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.5.1")

    // JUnit para pruebas unitarias
    testImplementation("junit:junit:4.13.2")

    // Mockito para pruebas unitarias y de integración
    testImplementation("org.mockito:mockito-core:4.4.0")
    androidTestImplementation("org.mockito:mockito-android:4.11.0")

    // Espresso para pruebas de UI
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // JUnit para pruebas instrumentadas de Android
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    testImplementation("org.mockito:mockito-core:3.12.4")
}