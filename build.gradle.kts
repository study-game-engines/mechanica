import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */

val lwjglVersion = "3.2.3"
val lwjglNatives = "natives-windows"

val kotlinVersion = "1.3.50"

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    `java-library`
    maven
}

val commonDependencies: DependencyHandlerScope.() -> Unit = {
    implementation("org.joml:joml:1.9.12")

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

val coreLwjgl: DependencyHandlerScope.() -> Unit = {
    //lwjgl
    api(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
}

val supplementaryLwjgl: DependencyHandlerScope.() -> Unit = {
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
}

allprojects {
    group = "com.mechanica.engine"
    version = 1.0

    repositories {
        // Use jcenter for resolving your dependencies.
        // You can declare any Maven/Ivy/file repository here.
        jcenter()

        mavenLocal()
        maven {
            url = uri("http://repo.maven.apache.org/maven2")
        }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    dependencies(commonDependencies)
}

project(":backend-lwjgl") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    dependencies {
        coreLwjgl()
        supplementaryLwjgl()
    }
}

project(":mechanica") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    dependencies(coreLwjgl)
}

project(":samples") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    dependencies(coreLwjgl)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    jvmTarget = "1.8"
}