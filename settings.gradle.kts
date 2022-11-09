rootProject.name = "Fresh Waves"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

include(":app")
includeBuild("submodules/handyauth") {
    dependencySubstitution {
        substitute(module("com.evanisnor.handyauth:client")).using(project(":client"))
    }
}