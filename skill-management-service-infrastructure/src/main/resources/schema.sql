CREATE TABLE IF NOT EXISTS skill (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL,
    total_technologies int,
    PRIMARY KEY (id),
    UNIQUE KEY name_skill_unique (name)
);

CREATE TABLE IF NOT EXISTS  skill_technology (
    skill_id BIGINT NOT NULL,
    technology_id BIGINT NOT NULL,
    CONSTRAINT skill_technology_pk PRIMARY KEY (skill_id,technology_id)
);