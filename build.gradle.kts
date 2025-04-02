
// region [[Basic Root Build Imports and Plugs]]

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*

plugins {
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinJvm) apply false
}

// endregion [[Basic Root Build Imports and Plugs]]

val enableJs = true
val enableNative = true

defaultBuildTemplateForRootProject(
  myLibDetails(
    name = "SMokK",
    description = "A bit scary library for mocking suspendable functions in Kotlin :-)",
    githubUrl = "https://github.com/langara/SMokK",
    version = Ver(0, 0, 10),
    // https://central.sonatype.com/artifact/pl.mareklangiewicz/smokk/versions/
    // https://github.com/mareklangiewicz/SMokK/releases
    settings = LibSettings(
      withJs = enableJs,
      withNativeLinux64 = enableNative,
      compose = null,
      withSonatypeOssPublishing = true,
    ),
  ),
)

// region [[Root Build Template]]

fun Project.defaultBuildTemplateForRootProject(details: LibDetails? = null) {
  details?.let {
    rootExtLibDetails = it
    defaultGroupAndVerAndDescription(it)
  }
}

// endregion [[Root Build Template]]
