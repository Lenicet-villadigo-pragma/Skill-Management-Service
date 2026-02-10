plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":skill-management-service-application"))
    implementation(project(":skill-management-service-domain"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
