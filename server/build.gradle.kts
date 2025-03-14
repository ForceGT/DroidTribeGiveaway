plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinxSerialization)
    application
}

group = "dev.gtxtreme.giveaway"
version = "1.0.0"
application {
    mainClass.set("dev.gtxtreme.giveaway.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {

    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.kotlin.serialization)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.websockets)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.zxing.qr.code)
    implementation(libs.zxing.qr.code.jvm)

    // CORS support
    implementation("io.ktor:ktor-server-cors:${libs.versions.ktor.get()}")

    // HTML DSL for Kotlin
    implementation(libs.kotlinx.html)

    testImplementation(libs.kotlin.test.junit)
}
