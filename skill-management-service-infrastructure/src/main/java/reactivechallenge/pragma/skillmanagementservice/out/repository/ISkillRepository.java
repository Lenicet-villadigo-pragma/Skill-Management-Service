package reactivechallenge.pragma.skillmanagementservice.out.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillEntity;
import reactor.core.publisher.Flux;

@Repository
public interface ISkillRepository extends R2dbcRepository<SkillEntity, Long> {
    Flux<SkillEntity> findAllBy(Pageable pageable);
}
