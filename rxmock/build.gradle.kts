plugins {
    `maven-publish`
    kotlin("jvm")
}

group = "com.github.langara.rxmock"
version = "0.0.1"

dependencies {
    implementation(Deps.kotlinStdlib)
    implementation(Deps.junit)
    implementation(Deps.rxjava)
}

// Create sources Jar from main kotlin sources
val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}

publishing {
    publications {
        create("default", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}
