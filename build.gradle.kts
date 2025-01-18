plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1" // или последняя версия Shadow
}

group = "me.rejomy"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    flatDir {
        dirs("libs")  // Указываем папку libs как источник зависимостей
    }
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly(files("libs/server.jar"))  // Указываем путь к JAR-файлу напрямую
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("com.github.cryptomorin:XSeries:11.0.0") { isTransitive = false }
}