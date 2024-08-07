plugins {
    id("stepMate.android.application")
    id("stepMate.android.compose")
    id("stepMate.android.parcelize")
    id("stepMate.android.gms-services")
}

android {
    namespace = "com.stepmate.app"

    defaultConfig {
        applicationId = "com.stepmate.app"
        targetSdk = 34
        versionCode = 11
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

}

dependencies {
    implementation(project(":design"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:home"))
    implementation(project(":feature:login"))
    implementation(project(":feature:mission"))
    implementation(project(":feature:core"))
    implementation(project(":feature:ranking"))
    implementation(project(":feature:profile"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.bundles.lifecycle)

    implementation(libs.androidx.health.connect.client)

    implementation(libs.bundles.workManager)
    ksp(libs.androidx.hilt.compiler)

    //debugImplementation("com.squareup.leakcanary:leakcanary-android:3.0-alpha-1")
}