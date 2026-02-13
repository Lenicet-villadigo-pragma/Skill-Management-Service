package reactivechallenge.pragma.skillmanagementservice.spi;

import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactor.core.publisher.Mono;

public interface ISkillRepositoryPort {
    Mono<SkillModel> save(SkillModel skillModel);

}
