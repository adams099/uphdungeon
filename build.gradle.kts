plugins {
    java
    application
    id("com.diffplug.spotless") version "8.4.0"
}

group = "id.uphdungeon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("id.uphdungeon.Main")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        eclipse().configFile("eclipse-format.xml")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
