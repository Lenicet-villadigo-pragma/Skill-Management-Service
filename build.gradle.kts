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

val springDocVersion = "2.6.0"
val r2dbcMysqlVersion = "1.0.2"
val squareupOkhttp3Version = "4.12.0"

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
        dependencies {
            dependency("org.springdoc:springdoc-openapi-starter-webflux-ui:${springDocVersion}")
            dependency("io.asyncer:r2dbc-mysql:${r2dbcMysqlVersion}")
            dependency("com.squareup.okhttp3:mockwebserver:${squareupOkhttp3Version}")
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