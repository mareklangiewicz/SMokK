import pl.mareklangiewicz.defaults.*
import pl.mareklangiewicz.ure.*
import pl.mareklangiewicz.utils.*

plugins {
    id("io.github.gradle-nexus.publish-plugin") version vers.nexusPublishGradlePlugin
    kotlin("multiplatform") version vers.kotlin apply false
}

defaultGroupAndVerAndDescription(libs.SMokK)

defaultSonatypeOssStuffFromSystemEnvs()

private val rootBuild = rootProjectPath / "build.gradle.kts"
private val smokkModuleBuild = rootProjectPath / "smokk" / "build.gradle.kts"
private val smokkxModuleBuild = rootProjectPath / "smokkx" / "build.gradle.kts"
private val kotlinSampleModuleBuild = rootProjectPath / "kotlinsample" / "build.gradle.kts"

tasks.registerAllThatGroupFun("inject",
    ::checkTemplates,
    ::injectTemplates,
)

fun checkTemplates() {
    checkRootBuildTemplate(rootBuild)
    checkKotlinModuleBuildTemplates(smokkModuleBuild, smokkxModuleBuild, kotlinSampleModuleBuild)
    checkMppModuleBuildTemplates(smokkModuleBuild, smokkxModuleBuild)
    checkJvmAppBuildTemplates(kotlinSampleModuleBuild)
}

fun injectTemplates() {
    injectRootBuildTemplate(rootBuild)
    injectKotlinModuleBuildTemplate(smokkModuleBuild, smokkxModuleBuild, kotlinSampleModuleBuild)
    injectMppModuleBuildTemplate(smokkModuleBuild, smokkxModuleBuild)
    injectJvmAppBuildTemplate(kotlinSampleModuleBuild)
}

// region [Root Build Template]

/**
 * System.getenv() should contain six env variables with given prefix, like:
 * * MYKOTLIBS_signing_keyId
 * * MYKOTLIBS_signing_password
 * * MYKOTLIBS_signing_key
 * * MYKOTLIBS_ossrhUsername
 * * MYKOTLIBS_ossrhPassword
 * * MYKOTLIBS_sonatypeStagingProfileId
 * * First three of these used in fun pl.mareklangiewicz.defaults.defaultSigning
 * * See deps.kt/template-mpp/template-mpp-lib/build.gradle.kts
 */
fun Project.defaultSonatypeOssStuffFromSystemEnvs(envKeyMatchPrefix: String = "MYKOTLIBS_") {
    ext.addAllFromSystemEnvs(envKeyMatchPrefix)
    defaultSonatypeOssNexusPublishing()
}

fun Project.defaultSonatypeOssNexusPublishing(
    sonatypeStagingProfileId: String = rootExt("sonatypeStagingProfileId"),
    ossrhUsername: String = rootExt("ossrhUsername"),
    ossrhPassword: String = rootExt("ossrhPassword"),
) = nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            stagingProfileId put sonatypeStagingProfileId
            username put ossrhUsername
            password put ossrhPassword
            nexusUrl put uri(repos.sonatypeOssNexus)
            snapshotRepositoryUrl put uri(repos.sonatypeOssSnapshots)
        }
    }
}

// endregion [Root Build Template]