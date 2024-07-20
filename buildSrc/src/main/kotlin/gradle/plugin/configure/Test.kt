package gradle.plugin.configure

import com.android.build.api.dsl.LibraryExtension
import gradle.plugin.getVersionCatalog
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

@Suppress("UnstableApiUsage")
internal fun Project.configureTest() {

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

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    val libs = getVersionCatalog()
    dependencies {
        "implementation"(libs.findLibrary("jetbrains-kotlinx-coroutines-core").get())
        "testImplementation"(libs.findLibrary("jetbrains-kotlinx-coroutines-test").get())
        "testImplementation"(libs.findBundle("testing").get())
        "testImplementation"(libs.findBundle("kotest").get())
        "testRuntimeOnly"(libs.findLibrary("junit.jupiter.engine").get())
    }
}