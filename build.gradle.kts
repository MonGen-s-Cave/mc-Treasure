plugins {
    id("java")
    id("com.gradleup.shadow") version("8.3.2")
    id("io.github.revxrsal.zapper") version("1.0.2")
    id("io.freefair.lombok") version("8.11")
    id("maven-publish")
}

group = "com.mongenscave"
version = "1.0.1"

repositories {
    maven {
        name = "MonGens-Cave"
        url = uri("https://repo.mongenscave.com/")
        credentials {
            username = project.findProperty("mongensUsername") as String
            password = project.findProperty("mongensPassword") as String
        }
    }

    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.artillex-studios.com/releases")
    maven("https://nexus.hc.to/content/repositories/pub_releases")
    maven("https://repo.mongenscave.com/releases")
}

dependencies {
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12") {
        exclude(module = "lamp.common")
        exclude(module = "lamp.brigadier")
    }

    zap("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    zap("io.github.revxrsal:lamp.brigadier:4.0.0-rc.12")
    zap("com.github.User-19fff:EasierChatSetup:7485c3412c")
    zap("org.bstats:bstats-bukkit:3.0.2")
    zap("com.github.Anon8281:UniversalScheduler:0.1.6")
    zap("dev.dejvokep:boosted-yaml:1.3.6")
    zap("com.mongenscave:mc-TimesAPI:1.0.0")

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.36")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.17")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

zapper {
    libsFolder = "libs"
    relocationPrefix = "com.mongenscave.mctreasure"

    repositories { includeProjectRepositories() }

    relocate("org.bstats", "bstats")
    relocate("com.github.Anon8281.universalScheduler", "universalScheduler")
    relocate("dev.dejvokep.boostedyaml", "boostedyaml")
}

tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

val apiJar = tasks.register<Jar>("apiJar") {
    archiveBaseName.set("mc-Treasure")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())

    from(sourceSets.main.get().output) {
        include("com/mongenscave/mctreasure/api/**")
    }
}

publishing {
    publications {
        create<MavenPublication>("apiJar") {
            artifact(apiJar.get()) {
                classifier = null
            }

            groupId = "com.mongenscave"
            artifactId = "mc-Treasure"
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "MonGens-Cave"
            url = uri("https://repo.mongenscave.com/releases")
            credentials {
                username = project.findProperty("mongensUsername") as String
                password = project.findProperty("mongensPassword") as String
            }
        }
    }
}

tasks.register("deployApi") {
    dependsOn("apiJar", "publishApiJarPublicationToMonGens-CaveRepository")
}