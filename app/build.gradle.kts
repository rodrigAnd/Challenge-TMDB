import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    id("kotlin-parcelize")
    id("jacoco")
}

android {
    namespace = "com.onboarding.mychallenge"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.onboarding.mychallenge"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isTestCoverageEnabled = true
        }
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
}

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

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.androidx.compiler)

    // Retrofit + OkHttp + Moshi
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    kapt(libs.moshi.kotlin.codegen)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.kotlin.test)
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

    // Configurar JVM target para evitar erro com Java > 20
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "17"
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

val jacocoFileFilter =
    listOf(
        // Arquivos gerados pelo Android/AGP
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        // Activities e Fragments (não testáveis unitariamente)
        "**/*Activity*",
        "**/*Fragment*",
        // Hilt
        "**/di/**",
        "**/hilt/**",
        "**/Hilt_*",
        "**/*_Hilt*",
        "**/*_Factory*",
        "**/*_MembersInjector*",
        "**/*_Provide*Factory*",
        // Data Binding / View Binding
        "**/databinding/**",
        "**/binding/**",
        "**/*_ViewBinding.class",
        // Classes de modelo que geralmente não têm lógica
        "**/models/**",
        // Mappers
        "**/*Mapper*",
        "**/*MapperImpl*",
        // ViewObjects
        "**/*ViewObject*",
        "**/*ViewObjectMapper*",
        // Application
        "**/*Application*",
    )

val jacocoDebugTree =
    fileTree("$buildDir/tmp/kotlin-classes/debug") {
        exclude(jacocoFileFilter)
    }

val jacocoMainSrc = files("$projectDir/src/main/java", "$projectDir/src/main/kotlin")

val jacocoExecData =
    fileTree(buildDir) {
        include("**/testDebugUnitTest.exec")
    }

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest")
    group = "verification"
    description = "Generates Jacoco code coverage reports for the debug build."

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    sourceDirectories.setFrom(jacocoMainSrc)
    classDirectories.setFrom(files(jacocoDebugTree))
    executionData.setFrom(files(jacocoExecData))
}

tasks.register("jacocoTestCoverageVerification", JacocoCoverageVerification::class) {
    dependsOn("jacocoTestReport")
    group = "verification"
    description = "Verifies Jacoco code coverage for the debug build."

    violationRules {
        rule {
            limit {
                // Mínimo de 70% de cobertura de instruções para aprovação
                minimum = "0.70".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            excludes =
                listOf(
                    "*.BuildConfig",
                    "*.R",
                    "*.R\$*",
                    "*.Manifest*",
                    "*.*_Factory",
                    "*.*_Hilt*",
                    "*.*_MembersInjector*",
                    "*.*_Provide*Factory*",
                    "*.*ViewBinding",
                    "*.*ViewBinding\$*",
                    "*.*Activity",
                    "*.*Fragment",
                    "*.*Application",
                )
            limit {
                counter = "INSTRUCTION"
                minimum = "0.70".toBigDecimal()
            }
        }
    }

    sourceDirectories.setFrom(jacocoMainSrc)
    classDirectories.setFrom(files(jacocoDebugTree))
    executionData.setFrom(files(jacocoExecData))
}
