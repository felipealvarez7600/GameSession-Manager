import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
    implementation(platform("org.http4k:http4k-bom:5.13.8.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile>{
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
        languageVersion = "1.9"
        freeCompilerArgs += listOf("-Xskip-prerelease-check", "-Xjsr305=strict")
    }
}

tasks.register<Copy>("copyRuntimeDependencies") {
    into("build/libs")
    from(configurations.runtimeClasspath)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "pt.isel.ls.AppKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}