import pl.mareklangiewicz.defaults.*

plugins {
    application
    kotlin("jvm")
}

repositories {
    defaultRepos()
}

application {
    mainClassName = "pl.mareklangiewicz.smokk.MainKt"
}

dependencies {
    implementation(deps.rxjava3)
    implementation(deps.rxrelay)
    implementation(deps.kotlinxCoroutinesCore)
    implementation(deps.kotlinxCoroutinesRx3)
    testImplementation(deps.junit5)
    testImplementation(deps.uspekx)
    testImplementation(project(":smokk"))
    testImplementation(project(":smokkx"))
//    testImplementation("com.github.langara:SMokK:0.0.2")
}

