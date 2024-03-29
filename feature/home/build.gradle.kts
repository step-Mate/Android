@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("stepMate.android.feature")
}

android {
    namespace = "jinproject.stepwalk.home"
}

dependencies {
    implementation(libs.androidx.health.connect.client)

    implementation(libs.bundles.workManager)
    ksp(libs.androidx.hilt.compiler)

    //implementation ("org.slf4j:slf4j-api:2.0.5")
    //implementation ("ch.qos.logback:logback-classic:1.3.1")
}