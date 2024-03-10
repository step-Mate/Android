package gradle.plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

internal class AndroidComposePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val android = extensions.findByType<LibraryExtension>()
            ?: extensions.findByType<ApplicationExtension>()

        android?.run {
            buildFeatures.compose = true
            composeOptions.kotlinCompilerExtensionVersion =
                libs.findVersion("composeCompiler").get().toString()
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            "implementation"(platform(bom))
            "androidTestImplementation"(platform(bom))

            "implementation"(libs.findBundle("compose").get())
            "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())

            "implementation"(libs.findLibrary("androidx.navigation.compose").get())
            "implementation"(libs.findBundle("composeAdaptive").get())
        }
    }
}
