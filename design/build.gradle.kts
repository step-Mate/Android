plugins {
    id("stepMate.android.library")
    id("stepMate.android.compose")
}

android {
    namespace = "jinproject.stepwalk.design"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.airbnb.android.lottie.compose)
}