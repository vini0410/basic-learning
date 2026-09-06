plugins {
    kotlin("jvm") version "2.3.0"
    application
}

group = "com.prototype.study"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() 
}

application {
    mainClass.set("com.prototype.study.MainKt")
}

dependencies {
    // Spring Boot Starter Web
    implementation("org.springframework.boot:spring-boot-starter-web:3.5.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.16")
    
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}