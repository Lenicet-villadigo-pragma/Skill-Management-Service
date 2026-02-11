package reactivechallenge.pragma.skillmanagementservice.out.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("skill_technology")
public record SkillTechnologyEntity(
        @Column("skill_id") Long skillId,
        @Column("technology_id") Long technologyId
) {}
