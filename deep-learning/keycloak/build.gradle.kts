plugins {
    `java`
}

val keycloakVersion = "24.0.5"

allprojects {
    group = "com.custom.keycloak"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    extra["keycloakVersion"] = keycloakVersion
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
