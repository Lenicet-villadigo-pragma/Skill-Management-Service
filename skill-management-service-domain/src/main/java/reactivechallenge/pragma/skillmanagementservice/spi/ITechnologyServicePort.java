package reactivechallenge.pragma.skillmanagementservice.spi;

import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyServicePort {
    Mono<Boolean> existsById(List<String> technologyIds);
    Flux<TechnologyExternalModel> getTechsByIds (List<String> techIds);
}
