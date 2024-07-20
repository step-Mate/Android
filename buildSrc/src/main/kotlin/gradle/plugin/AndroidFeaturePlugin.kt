package gradle.plugin

import gradle.plugin.configure.configureTest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeaturePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("stepMate.android.library")
            apply("stepMate.android.hilt")
            apply("stepMate.android.compose")
        }

        configureTest()

        val libs = getVersionCatalog()
        dependencies {
            "implementation"(project(":domain"))
            "implementation"(project(":design"))
            "implementation"(project(":feature:core"))

            "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
            "implementation"(libs.findBundle("lifecycle").get())
            "implementation"(libs.findLibrary("jetbraints-kotlinx-collections-immutable").get())
        }
    }
}
