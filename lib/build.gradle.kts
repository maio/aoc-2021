plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"

    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.jetbrains.kotlin:kotlin-script-runtime")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("com.approvaltests:approvaltests:12.3.1")
}

tasks {
    test {
        useJUnitPlatform()
    }
}