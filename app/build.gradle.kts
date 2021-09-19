plugins {
    id("com.android.application")
    id("kotlin-android")
}
println("--------build.gradle.kts---")

android {

    compileSdkVersion(30)

    defaultConfig {
        applicationId("com.dzc.chunk")
        minSdkVersion(21)
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = ("1.8")
    }
    buildFeatures {
        viewBinding = true
    }
}
dependencies {

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
    implementation ("androidx.core:core-ktx:1.3.2")
    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("com.google.android.material:material:1.3.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")

}




