plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.todonotion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.todonotion"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }
        debug {
            isDebuggable = true
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
        dataBinding = true
        viewBinding = true
    }

}

dependencies {
    // Define versions in a single place
    val coroutines_version = "1.7.1"
    val room_version = "2.5.0"
    val archTestingVersion = "2.1.0"
    val androidXTestExtKotlinRunnerVersion = "1.1.3"
    val androidXTestCoreVersion = "1.4.0"
    val dagger_version = "2.44"


    // App dependencies
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.test.espresso:espresso-contrib:3.4.0")
    implementation("androidx.test.ext:junit-ktx:1.1.5")


    // Dependencies for local unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.12.4")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    // Dependencies for Android instrumented unit tests
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation ("androidx.arch.core:core-testing:$archTestingVersion")

    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")

    // Testing code should not be included in the main code.
    debugImplementation("androidx.fragment:fragment-testing:1.5.3")
    implementation ("androidx.test:core:$androidXTestCoreVersion")

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.9.3")

    // Retrofit with Moshi Converter
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Coil
    implementation("io.coil-kt:coil:2.2.2")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.2")

    // Room libraries
    implementation("androidx.room:room-runtime:$room_version")
   // annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    //kapt("androidx.room:room-compiler:$room_version")
    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    //https://github.com/google/flexbox-layout
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    //https://stackoverflow.com/questions/44354355/unresolved-reference-async-in-kotlin
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1")

    //https://developer.android.com/develop/ui/views/touch-and-input/swipe/add-swipe-interface
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    //https://developer.android.com/codelabs/basic-android-kotlin-training-adaptive-layouts?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-kotlin-unit-3-pathway-5%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-training-adaptive-layouts#5
    implementation("androidx.slidingpanelayout:slidingpanelayout:1.2.0-beta01")

    // AndroidX Test - JVM testing
    testImplementation ("androidx.test:core-ktx:$androidXTestCoreVersion")
    testImplementation ("androidx.test.ext:junit:$androidXTestExtKotlinRunnerVersion")

    // WorkManager dependency
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries


    // Dagger
    implementation("com.google.dagger:dagger:$dagger_version")
    kapt("com.google.dagger:dagger-compiler:$dagger_version")

    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.4.2")

    kaptAndroidTest("com.google.dagger:dagger-compiler:$dagger_version")


}