@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("stepMate.android.feature")
}

android {
    namespace = "jinproject.stepwalk.profile"
}

dependencies {
    implementation(libs.airbnb.android.lottie.compose)
}