import org.jetbrains.kotlin.gradle.plugin.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*

plugins {
    kotlin("jvm")
    application
}

defaultBuildTemplateForJvmApp(appMainPackage = "pl.mareklangiewicz.smokk")

dependencies {
    implementation(deps.rxjava3)
    implementation(deps.rxrelay)
    implementation(deps.kotlinxCoroutinesCore)
    implementation(deps.kotlinxCoroutinesRx3)
    testImplementation(deps.junit5)
    testImplementation(deps.uspekx)
    testImplementation(project(":smokk"))
    testImplementation(project(":smokkx"))
}

// region [Kotlin Module Build Template]

fun TaskCollection<Task>.defaultKotlinCompileOptions(
    jvmTargetVer: String = vers.defaultJvm,
    requiresOptIn: Boolean = true
) = withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = jvmTargetVer
        if (requiresOptIn) freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

// endregion [Kotlin Module Build Template]

// region [Jvm App Build Template]

@Suppress("UNUSED_VARIABLE")
fun Project.defaultBuildTemplateForJvmApp(
    appMainPackage: String,
    appMainClass: String = "MainKt",
    details: LibDetails = libs.Unknown,
    withTestJUnit5: Boolean = true,
    withTestUSpekX: Boolean = true,
    addMainDependencies: KotlinDependencyHandler.() -> Unit = {}
) {
    repositories { defaultRepos() }
    defaultGroupAndVerAndDescription(details)

    kotlin {
        sourceSets {
            val main by getting {
                dependencies {
                    addMainDependencies()
                }
            }
            val test by getting {
                dependencies {
                    if (withTestJUnit5) implementation(deps.junit5engine)
                    if (withTestUSpekX) implementation(deps.uspekx)
                }
            }
        }
    }

    application { mainClass put "$appMainPackage.$appMainClass" }

    tasks.defaultKotlinCompileOptions()
    tasks.defaultTestsOptions(onJvmUseJUnitPlatform = withTestJUnit5)
}

// endregion [Jvm App Build Template]