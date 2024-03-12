plugins {
    id("stepMate.android.library")
    id("stepMate.android.compose")
}

android {
    namespace = "com.stepmate.design"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.airbnb.android.lottie.compose)
}