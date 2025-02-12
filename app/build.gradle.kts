plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tutoriapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tutoriapp"
        minSdk = 28
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

    implementation(libs.material)
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.json:json:20211205")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.belerweb:pinyin4j:2.5.1")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation(libs.firebase.auth)
    //---

}