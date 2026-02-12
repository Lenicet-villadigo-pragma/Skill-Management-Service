plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":skill-management-service-application"))
    implementation(project(":skill-management-service-domain"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui")
    implementation("io.r2dbc:r2dbc-pool")
    runtimeOnly("io.asyncer:r2dbc-mysql")
}
