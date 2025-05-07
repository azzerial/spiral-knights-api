rootProject.name = "spiral-knights-api"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            library("annotations", "org.jetbrains", "annotations").version("26.0.2")
            library("slf4j", "org.slf4j", "slf4j-api").version("2.0.17")
            library("logback", "ch.qos.logback", "logback-classic").version("1.3.15")
            library("skhc", "net.azzerial", "spiral-knights-headless-client").version("1.0.0")
            library("javalin", "io.javalin", "javalin").version("6.6.0")
            library("jackson", "com.fasterxml.jackson.core", "jackson-databind").version("2.17.2")
            library("openapi-annotation", "io.javalin.community.openapi", "openapi-annotation-processor").version("6.5.0")
            library("openapi", "io.javalin.community.openapi", "javalin-openapi-plugin").version("6.5.0")
            library("swagger", "io.javalin.community.openapi", "javalin-swagger-plugin").version("6.5.0")
        }
    }
}