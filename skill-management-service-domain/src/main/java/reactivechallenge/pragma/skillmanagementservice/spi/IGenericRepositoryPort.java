package reactivechallenge.pragma.skillmanagementservice.spi;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Generic repository port interface for reactive data operations.
 * This interface follows the hexagonal architecture pattern and defines
 * the contract for persistence operations.
 *
 * @param <T> the domain entity type
 * @param <I> the type of the entity identifier
 */
public interface IGenericRepositoryPort<T, I> {

    Mono<T> save(T entity);
    Mono<T> findById(I id);
    Flux<T> findAll();
    Mono<Void> deleteById(I id);
    Mono<Void> delete(T entity);
    Mono<Boolean> existsById(I id);
    Mono<Long> count();
    Mono<Void> deleteAll();
}
