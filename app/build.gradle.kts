plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)

    kotlin("kapt")
}

android {
    namespace = "com.wei.music"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wei.music"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

//    sourceSets {
//        named("main") {
//            java.srcDir(layout.buildDirectory.dir("generated/source/deps"))
//        }
//    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":myutil"))
    implementation(fileTree("libs") { include("*.jar") })

    implementation("com.blankj:utilcodex:1.31.1")

    // Hilt
    implementation(libs.jakarta.inject.api)
    implementation("com.google.dagger:hilt-android:2.51")
    kapt("com.google.dagger:hilt-compiler:2.51")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-rxjava3:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // 其余所有依赖（顺序随意）
    implementation(libs.androidx.core.ktx)
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.6")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxjava:3.1.5")
    implementation("com.uber.autodispose2:autodispose:2.1.1")
    implementation("com.uber.autodispose2:autodispose-android:2.1.1")
    implementation("com.uber.autodispose2:autodispose-androidx-lifecycle:2.1.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.media:media:1.7.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("androidx.palette:palette:1.0.0")
    implementation("com.tencent:mmkv-static:1.2.10")
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("com.baidu.mobstat:mtj-sdk:4.0.0.7")
    implementation("ren.qinc.edgetranslucent:lib:0.0.3")
    implementation("com.drakeet.drawer:drawer:1.0.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}