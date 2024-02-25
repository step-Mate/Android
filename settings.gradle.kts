pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Stepwalk"
include(":app")
include(":design")
include(":domain")
include(":feature")
include(":feature:home")
include(":data")
include(":feature:login")
include(":feature:mission")
include(":feature:core")
include(":feature:profile")
