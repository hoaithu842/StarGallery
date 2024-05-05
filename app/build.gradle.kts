plugins {
    id("com.android.application")
}

android {
    namespace = "vn.edu.hcmus.stargallery"
    compileSdk = 34

    defaultConfig {
        applicationId = "vn.edu.hcmus.stargallery"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = true
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
    def activity_version = "1.9.0"


    implementation ("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.exifinterface:exifinterface:1.3.2")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Add the AAR dependency
    implementation(files("./libs/ds-photo-editor-sdk-v10.aar"))

    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("io.reactivex.rxjava2:rxjava:2.1.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation "androidx.activity:activity-ktx:$activity_version"
}