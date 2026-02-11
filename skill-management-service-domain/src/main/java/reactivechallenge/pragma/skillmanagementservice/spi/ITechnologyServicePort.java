package reactivechallenge.pragma.skillmanagementservice.spi;

import reactor.core.publisher.Mono;

public interface ITechnologyServicePort {
    Mono<Boolean> existsById(Long technologyId);
}
