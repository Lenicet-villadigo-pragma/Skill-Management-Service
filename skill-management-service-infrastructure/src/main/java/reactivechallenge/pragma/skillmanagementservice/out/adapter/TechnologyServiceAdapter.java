package reactivechallenge.pragma.skillmanagementservice.out.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactivechallenge.pragma.skillmanagementservice.exception.InconsistencyDataException;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactor.core.publisher.Mono;

@Component
public class TechnologyServiceAdapter implements ITechnologyServicePort {

    private final WebClient webClient;

    public TechnologyServiceAdapter(WebClient.Builder webClientBuilder,
                                    @Value("${external.services.technology.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<Boolean> existsById(Long technologyId) {
        return webClient.get()
                .uri("/exists/{techId}", technologyId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorMap(e -> new InconsistencyDataException(String.format("Error al verificar la existencia de la tecnología con id %d ",technologyId)));
    }
}
