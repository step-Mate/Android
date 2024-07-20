plugins {
    id("stepMate.android.feature")
}

android {
    namespace = "com.stepmate.home"
}

dependencies {
    implementation(libs.androidx.health.connect.client)

    implementation(libs.bundles.workManager)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.airbnb.android.lottie.compose)

    implementation(libs.androidx.lifecycle.service)

    implementation(libs.bundles.windowManager)

    //implementation ("org.slf4j:slf4j-api:2.0.5")
    //implementation ("ch.qos.logback:logback-classic:1.3.1")
}