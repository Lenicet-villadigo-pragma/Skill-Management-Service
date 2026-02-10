rootProject.name = "Skill-Management-Service"

include(
    "skill-management-service-domain",
    "skill-management-service-application",
    "skill-management-service-infrastructure"
)


project(":skill-management-service-domain").projectDir = file("skill-management-service-domain")
project(":skill-management-service-application").projectDir = file("skill-management-service-application")
project(":skill-management-service-infrastructure").projectDir = file("skill-management-service-infrastructure")