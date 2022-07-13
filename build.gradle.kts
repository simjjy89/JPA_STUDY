plugins {
    kotlin("jvm") version "1.5.31"
}

group = "jpa-basic"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    implementation("org.hibernate:hibernate-entitymanager:5.6.8.Final")
    implementation("com.h2database:h2:2.1.212")
}