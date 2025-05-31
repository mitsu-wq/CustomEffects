plugins {
    kotlin("jvm") version "2.2.0-RC"
}

group = "me.deadybbb"
version = "0.1.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:10.0.1")
    compileOnly(files("./libs/YbbbBasicModule-0.1.5.jar"))
}

kotlin {
    jvmToolchain(17)
}