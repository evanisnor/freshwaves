dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Fresh Waves"
include(":app")
includeBuild("submodules/handyauth") {
    dependencySubstitution {
        substitute(module("com.evanisnor.handyauth:client")).using(project(":client"))
    }
}