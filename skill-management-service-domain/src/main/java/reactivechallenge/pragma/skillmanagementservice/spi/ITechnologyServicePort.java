package reactivechallenge.pragma.skillmanagementservice.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ITechnologyServicePort {
    Mono<Boolean> existsById(List<String> technologyIds);
}
