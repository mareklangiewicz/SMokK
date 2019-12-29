import java.net.URI

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.smokk.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib8)
    implementation(Deps.rxjava)
    implementation(Deps.rxrelay)
    implementation(Deps.kotlinxCoroutinesCore)
    implementation(Deps.kotlinxCoroutinesRx2)
    testImplementation(Deps.junit)
    testImplementation(Deps.uspek)
    testImplementation(project(":smokk"))
    testImplementation(project(":smokkx"))
//    testImplementation("com.github.langara:SMokK:0.0.2")
}

