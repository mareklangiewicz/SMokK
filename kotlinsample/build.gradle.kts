import java.net.URI

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.rxmock.MainKt"
}

dependencies {
    implementation(Deps.kotlinStdlib)
    implementation(Deps.junit)
    testImplementation(Deps.uspek)
    testImplementation(Deps.rxjava)
    testImplementation(Deps.rxrelay)
    testImplementation(project(":rxmock"))
//    testImplementation("com.github.langara:RxMock:master-SNAPSHOT")
}

