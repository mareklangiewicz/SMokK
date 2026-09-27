
// region [[Basic Root Build Imports and Plugs]]

import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.utils.*
import pl.mareklangiewicz.deps.*
import pl.mareklangiewicz.templatefun.*

plugins {
  plug(plugs.TemplateFun) apply false
  plug(plugs.KotlinMulti) apply false
  plug(plugs.KotlinJvm) apply false

  // Resolve the publish plugin ONCE here, with its version. Without this the only source of
  // it is the templatefun plugin's own classpath (templatefun depends on it), which Gradle sees as
  // "unknown version" -- and then a versioned request in a subproject cannot be checked
  // against it. template-full and template-andro declare it here for the same reason.
  plug(plugs.VannikPublish) apply false
}

// endregion [[Basic Root Build Imports and Plugs]]

defaultGroupAndVerAndDescription(gradle.extLib)
