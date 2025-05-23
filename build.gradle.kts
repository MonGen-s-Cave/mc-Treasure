plugins {
    id("java")
    id("com.gradleup.shadow") version("8.3.2")
    id("io.github.revxrsal.zapper") version("1.0.2")
    id("io.freefair.lombok") version("8.11")
}

group = "net.netherpulse"
version = "1.0.0"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.artillex-studios.com/releases")
    maven ("https://nexus.hc.to/content/repositories/pub_releases")
}

dependencies {
    implementation("com.artillexstudios.axapi:axapi:1.4.557:all")
    zap("com.github.User-19fff:EasierChatSetup:7485c3412c")

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.17")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

zapper {
    libsFolder = "libs"
    relocationPrefix = "net.netherpulse.nptreasure"

    repositories { includeProjectRepositories() }

    relocate("com.artillexstudios.axapi", "axapi")
}