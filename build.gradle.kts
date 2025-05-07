plugins {
    application

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.azzerial"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.azzerial.net/releases") }
}

dependencies {
    implementation(libraries.annotations)
    implementation(libraries.slf4j)
    implementation(libraries.logback)
    implementation(libraries.skhc)
    implementation(libraries.javalin)
    implementation(libraries.jackson)
    annotationProcessor(libraries.openapi.annotation)
    implementation(libraries.openapi)
    implementation(libraries.swagger)
}

application {
    mainClass.set("net.azzerial.ska.Main")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.shadowJar {
    archiveClassifier.set("withDependencies")
    exclude("*.pom")
}

val run by tasks.getting(JavaExec::class) {
    val envFile = file(".env")

    if (envFile.exists()) {
        envFile.forEachLine {
            if (it.matches(Regex("[^#][^=]+=.*"))) {
                val (key, value) = it.split("=")
                environment(key, value)
            }
        }
    }
}
