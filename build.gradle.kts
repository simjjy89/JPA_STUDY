buildscript {
    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-noarg:1.5.31")
    }
}

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"
    kotlin("plugin.allopen") version "1.5.10"
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
   // runtimeOnly("com.h2database:h2")
    implementation("org.hibernate:hibernate-entitymanager:5.3.11.Final")
    implementation(kotlin("stdlib"))
    implementation ("com.h2database:h2:2.1.214")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.5.31")
}

noArg {
    annotation("com.my.Annotation")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}