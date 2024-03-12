plugins {
    id("stepMate.android.library")
    id("stepMate.android.compose")
}

android {
    namespace = "com.stepmate.core"
}

dependencies {
    implementation(project(":domain"))
}