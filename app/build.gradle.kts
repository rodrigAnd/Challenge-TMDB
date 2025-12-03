plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    id("kotlin-parcelize")
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    // Firebase Crashlytics - descomente quando adicionar google-services.json
    // alias(libs.plugins.google.services)
    // alias(libs.plugins.firebase.crashlytics)
    // JaCoCo temporariamente desabilitado devido a conflito com AGP 8.7.3
    // id("org.gradle.jacoco")
}

android {
    namespace = "com.onboarding.mychallenge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.onboarding.mychallenge"
        minSdk = 26
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs +=
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
            )
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// Configuração do JaCoCo - temporariamente desabilitado devido a conflito com AGP 8.7.3
// jacoco {
//     toolVersion = "0.8.12"
// }

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.coil)
    implementation(libs.shimmer)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.androidx.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    kapt(libs.moshi.kotlin.codegen)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(libs.kotlinx.coroutines.android)

    // Firebase Crashlytics - descomente quando adicionar google-services.json
    // implementation(platform(libs.firebase.bom))
    // implementation(libs.firebase.crashlytics.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

ktlint {
    version.set("1.0.1")
    debug.set(false)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
        include("**/kotlin/**")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false

    val detektConfigFile = file("$projectDir/../config/detekt/detekt.yml")
    if (detektConfigFile.exists()) {
        config.setFrom(detektConfigFile)
    }

    val baselineFile = file("$projectDir/../config/detekt/baseline.xml")
    if (baselineFile.exists()) {
        baseline = baselineFile
    }

    // Configurar JVM target para evitar erro com Java 24
    // O Detekt usa o jvmTarget do Kotlin, que já está configurado como "17" em kotlinOptions
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        // Forçar uso do JVM target do Kotlin (17)
        setProperty("jvmTarget", "17")
    }
}
