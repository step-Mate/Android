package gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidGmsServicePlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = target.extensions.getByType<VersionCatalogsExtension>().named("libs")

        with(pluginManager) {
            apply("com.google.gms.google-services")
            apply("com.google.firebase.crashlytics")
        }

        dependencies {
            "implementation"(platform(libs.findLibrary("google.firebase.bom").get()))
            "implementation"(libs.findLibrary("google.firebase.analytics").get())
            "implementation"(libs.findLibrary("google.firebase.crashlytics").get())
        }
    }
}