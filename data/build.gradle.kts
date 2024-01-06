@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("stepMate.android.library")
    id("stepMate.android.hilt")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "jinproject.stepwalk.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.bundles.datastore)
}

protobuf {

    protoc {
        artifact = "com.google.protobuf:protoc:3.23.4"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}