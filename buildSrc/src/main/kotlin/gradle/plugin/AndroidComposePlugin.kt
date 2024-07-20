package gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidComposePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = getVersionCatalog()

        androidExtension.run {
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
