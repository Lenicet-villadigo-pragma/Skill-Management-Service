package reactivechallenge.pragma.skillmanagementservice.api;

import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactor.core.publisher.Mono;

public interface IRegisterSkillServicePort {
    Mono<SkillModel> registerSkill(SkillModel skillModel);
}
