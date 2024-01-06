package gradle.plugin

import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal class AndroidHiltPlugin : Plugin<Project> {
    
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        with(pluginManager) {
            apply("com.google.dagger.hilt.android")
            apply("com.google.devtools.ksp")
        }

        dependencies {
            "implementation"(libs.findLibrary("google.dagger.hilt.android").get())
            "ksp"(libs.findLibrary("google.dagger.hilt.android.compiler").get())
        }
    }
}
