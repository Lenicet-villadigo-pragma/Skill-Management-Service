package reactivechallenge.pragma.skillmanagementservice.api;

import reactor.core.publisher.Mono;

import java.util.List;

public interface IDeleteSkillServicePort {
    Mono<Void> deleteSkillsByIds(List<Long> skillIds);
}
