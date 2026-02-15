package reactivechallenge.pragma.skillmanagementservice.out.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("skill")
public record SkillEntity(
        @Id @Column("id") Long id,
        @Column("name") String name,
        @Column("description") String description,
        @Column("total_technologies") Integer totalTechnologies
) {}