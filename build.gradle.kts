plugins {
    java
    kotlin("jvm") version "1.4.21"
    application
    kotlin("plugin.serialization") version "1.4.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val coroutinesVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val kotlinSerializationVersion: String by project
val exposedVersion: String by project

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://dl.bintray.com/kotlin/kotlin-dev")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}
dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // Ktor
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")

    // Logs
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")

    // DB
    implementation("com.h2database", "h2", "1.3.148")
    // ORM
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    // Tests
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
