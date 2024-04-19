package gradle.plugin

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.getByType

internal class AndroidFeaturePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("stepMate.android.library")
            apply("stepMate.android.hilt")
            apply("stepMate.android.compose")
        }

        extensions.configure<LibraryExtension> {
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            testOptions {
                unitTests.all {
                    it.useJUnitPlatform()
                }
            }
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            "implementation"(project(":domain"))
            "implementation"(project(":design"))
            "implementation"(project(":feature:core"))

            "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
            "implementation"(libs.findBundle("lifecycle").get())
            "implementation"(libs.findBundle("windowManager").get())

            "testImplementation"(libs.findBundle("testing").get())
            "testImplementation"(libs.findBundle("kotest").get())
            "testRuntimeOnly"(libs.findLibrary("junit.jupiter.engine").get())
        }
    }
}
