
// region [[Basic MPP App Build Imports and Plugs]]

import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*
import com.vanniktech.maven.publish.*
import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plugAll(plugs.TemplateFunNoVer, plugs.KotlinMulti, plugs.VannikPublish)
}

// endregion [[Basic MPP App Build Imports and Plugs]]

defaultBuildTemplateForBasicMppApp {
  implementation(project(":smokk"))
  implementation(project(":smokkx"))
}

kotlin {
  sourceSets {
    jvmMain {
      dependencies {
        implementation(KotlinX.coroutines_rx3)
      }
    }
  }
}
