import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.bedshanty"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    pom {
        name.set("kregex")
        description.set("A Kotlin DSL library for building Regex easily")
        url.set("https://github.com/bedshanty/kregex")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("bedshanty")
                name.set("Ji Heon Lee")
                email.set("bedshanty@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/bedshanty/kregex.git")
            developerConnection.set("scm:git:ssh://github.com/bedshanty/kregex.git")
            url.set("https://github.com/bedshanty/kregex")
        }
    }
}
