package reactivechallenge.pragma.skillmanagementservice.spi;

import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ISkillRepositoryPort {
    Mono<SkillModel> save(SkillModel skillModel);
    Flux<SkillModel> getSkills(SkillSortField skillSortField, SkillSortOrder skillSortOrder
            , Integer pageNumber, Integer pageSize);
    Mono<Long> countSkills();
    Mono<Boolean> exists(List<Long> ids);
    Flux<SkillModel> getSkillsById(List<Long> ids);
}
