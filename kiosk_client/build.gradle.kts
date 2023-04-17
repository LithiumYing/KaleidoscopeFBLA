import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("java-library")
    id("com.apollographql.apollo3").version("3.7.5")
    kotlin("jvm") version "1.6.10"
}

repositories {
    mavenCentral()
    jcenter()
     maven("https://jitpack.io")
}

dependencies {
    implementation("com.apollographql.apollo3:apollo-runtime:3.7.5")
    implementation(kotlin("stdlib-jdk8"))
    // implementation("com.github.javadev:jdatepicker:1.3.4")
    // implementation("org.graalvm.js:js:22.3.1")
    // implementation("com.apollographql.apollo3:apollo3-coroutines:3.7.5")
    // implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:13.0.0")
    // implementation("com.graphql-java:graphql-java:16.0")
    // implementation("com.graphql-java-kickstart:graphql-webclient-spring-boot-starter:2.0.0")
    // implementation("org.springframework.boot:spring-boot-starter-web:3.0.5")
    // implementation("org.springframework.boot:spring-boot-starter-webflux:3.0.5")
    // implementation("org.springframework:spring-webmvc:6.0.7")
    // implementation("io.projectreactor:reactor-core:3.5.4")
    // implementation ("com.squareup.okhttp3:okhttp:4.10.0")
      implementation("org.openpnp:opencv:4.5.1-2")
    implementation("com.opencsv:opencsv:5.7.1")
    implementation("com.google.zxing:core:3.5.1")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
    // implementation(":webcamqrcodescanner")
    implementation("com.github.sarxos:webcam-capture:0.3.12")
    implementation("com.google.zxing:javase:3.5.1")
    // implementation("org.slf4j:slf4j-api:2.0.5")
    // implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("org.json:json:20201115")
    //  implementation("org.netbeans:org-netbeans-libs-absolute-layout:RELEASE170")

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

// sourceSets {
//     main {
//         java {
//             srcDir ('Kaleidoscope-1/kiosk_client/app/src/main/java/io/github/lithiumying/kioskclient/')
//         }
//     }
// }

// val compileJava: JavaCompile by tasks
// compileJava.sourceCompatibility = "17"
// compileJava.targetCompatibility = "17"

// tasks.withType<KotlinCompile> {
//     kotlinOptions.jvmTarget = "17"
// }
// tasks.jar {
//     manifest {
//         attributes["Main-Class"] = "io.github.lithiumying.kioskclient.Main"
//     }
// }
// https://stackoverflow.com/questions/46419817/how-to-add-new-sourceset-with-gradle-kotlin-dsl
java.sourceSets["main"].java {
    srcDir("app/src/main/java/")
}

