package reactivechallenge.pragma.skillmanagementservice.out.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillEntity;

@Repository
public interface ISkillRepository extends R2dbcRepository<SkillEntity, Long> {
}
