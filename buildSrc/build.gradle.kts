plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.android)
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.hilt)
    implementation(libs.gradle.google.devtools.ksp)
    implementation(libs.gradle.google.gms.google.services)
    implementation(libs.gradle.google.firebase.crashlytics)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "stepMate.android.application"
            implementationClass = "gradle.plugin.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "stepMate.android.library"
            implementationClass = "gradle.plugin.AndroidLibraryPlugin"
        }
        register("androidHilt") {
            id = "stepMate.android.hilt"
            implementationClass = "gradle.plugin.AndroidHiltPlugin"
        }
        register("androidCompose") {
            id = "stepMate.android.compose"
            implementationClass = "gradle.plugin.AndroidComposePlugin"
        }
        register("androidFeature") {
            id = "stepMate.android.feature"
            implementationClass = "gradle.plugin.AndroidFeaturePlugin"
        }
        register("androidParcelize") {
            id = "stepMate.android.parcelize"
            implementationClass = "gradle.plugin.AndroidParcelizePlugin"
        }
        register("androidGmsService") {
            id = "stepMate.android.gms-services"
            implementationClass = "gradle.plugin.AndroidGmsServicePlugin"
        }
    }
}
