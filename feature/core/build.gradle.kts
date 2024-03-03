plugins {
    id("stepMate.android.library")
    id("stepMate.android.compose")
}

android {
    namespace = "jinproject.stepwalk.core"
}

dependencies {
    implementation(project(":domain"))
}