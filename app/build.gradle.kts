plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "br.com.cenariovivo.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.cenariovivo.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("CENARIOVIVO_UPLOAD_STORE_FILE") ?: project.property("CENARIOVIVO_UPLOAD_STORE_FILE") as String)
            storePassword = System.getenv("CENARIOVIVO_UPLOAD_STORE_PASSWORD") ?: project.property("CENARIOVIVO_UPLOAD_STORE_PASSWORD") as String
            keyAlias = System.getenv("CENARIOVIVO_UPLOAD_KEY_ALIAS") ?: project.property("CENARIOVIVO_UPLOAD_KEY_ALIAS") as String
            keyPassword = System.getenv("CENARIOVIVO_UPLOAD_KEY_PASSWORD") ?: project.property("CENARIOVIVO_UPLOAD_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Habilitar minificação e ofuscação
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release") // Associar a config de assinatura ao build de release
        }
        debug {
            // Configurações de debug, geralmente não precisam de assinatura de release
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("com.google.firebase:firebase-messaging")
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    //implementation("com.google.firebase:firebase-analytics-ktx") // Se você já tem analytics
   //implementation("com.google.firebase:firebase-messaging-ktx") // <--- VERIFIQUE ESTA LINHA

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}