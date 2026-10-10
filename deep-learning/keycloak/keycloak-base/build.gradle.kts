val dockerBuildDir = layout.buildDirectory.dir("docker")

val copyDockerFiles = tasks.register<Copy>("copyDockerFiles") {
    from("Dockerfile")
    into(dockerBuildDir)
}

val copyProviders = tasks.register<Copy>("copyProviders") {
    dependsOn(":keycloak-custom:jar")
    from(project(":keycloak-custom").tasks.named("jar"))
    into(dockerBuildDir.map { it.dir("providers") })
}

tasks.register("prepareDocker") {
    dependsOn(copyDockerFiles, copyProviders)
    description = "Prepara o diretório build/docker com o Dockerfile e os providers compilados"
    group = "docker"
}