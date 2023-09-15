import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("java-library")
    id("com.apollographql.apollo3").version("3.7.5")
    kotlin("jvm") version "1.6.10"
    id("org.openjfx.javafxplugin") version "0.0.10"
    id("application")
}

javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.web", "javafx.swing")
}

repositories {
    mavenCentral()
    jcenter()
     maven("https://jitpack.io")
}

dependencies {
    implementation("com.apollographql.apollo3:apollo-runtime:3.7.5")
    implementation(kotlin("stdlib-jdk8"))
      implementation("org.openpnp:opencv:4.5.1-2")
    implementation("com.opencsv:opencsv:5.7.1")
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.github.sarxos:webcam-capture:0.3.12")
    implementation("com.google.zxing:javase:3.5.1")
    implementation("org.json:json:20201115")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    // implementation("io.github.g00fy2.quickie:quickie-bundled:1.6.0")
        implementation("org.openjfx:javafx-controls:17")
    implementation("org.openjfx:javafx-web:17")
    implementation("org.openjfx:javafx-swing:11-ea+24")
      implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.pushing-pixels:radiance-substance:4.5.0")
}


application {
    mainClass.set("JavaFXWebViewExample")
}

apollo {
    generateKotlinModels.set(true)
    service("service") {
        packageName.set("com.example.rocketreserver")
        schemaFile.set(file("app/src/main/graphql/schema.graphqls"))
        mapScalarToUpload("Upload")
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}
// https://stackoverflow.com/questions/46419817/how-to-add-new-sourceset-with-gradle-kotlin-dsl
java.sourceSets["main"].java {
    srcDir("app/src/main/java/")
}

