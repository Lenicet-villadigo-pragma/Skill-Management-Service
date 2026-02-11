package reactivechallenge.pragma.skillmanagementservice.out.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillTechnologyEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ISkillTechnologyRepository extends ReactiveCrudRepository<SkillTechnologyEntity, Long> {
    Mono<Void> deleteBySkillId(Long skillId);
    Flux<SkillTechnologyEntity> findAllBySkillId(Long skillId);
}