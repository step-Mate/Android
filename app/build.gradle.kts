plugins {
    id("stepMate.android.application")
    id("stepMate.android.compose")
    id("stepMate.android.parcelize")
}

android {
    namespace = "jinproject.stepwalk.app"

    defaultConfig {
        applicationId = "jinproject.stepwalk.app"
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }

    configurations.all {
        resolutionStrategy {
            exclude(
                group = "org.jetbrains.kotlinx",
                module = "kotlinx-coroutines-debug"
            )
        }
    }
}

dependencies {

    implementation(project(":design"))
    implementation(project(":domain"))
    implementation(project(":feature:home"))
    implementation(project(":data"))
    implementation(project(":feature:login"))
    implementation(project(":feature:mission"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.activity.compose)

    implementation(libs.bundles.navigation)

    implementation(libs.bundles.lifecycle)

    implementation(libs.androidx.health.connect.client)

    implementation(libs.bundles.workManager)
    ksp(libs.androidx.hilt.compiler)

}