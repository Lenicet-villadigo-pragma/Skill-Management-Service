import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    id("org.springframework.boot") version "3.5.10" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    java
}

allprojects {
    group = "reactivechallenge.pragma"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}