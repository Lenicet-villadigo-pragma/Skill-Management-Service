package reactivechallenge.pragma.skillmanagementservice.out.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;
import reactivechallenge.pragma.skillmanagementservice.exception.InconsistencyDataException;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class TechnologyServiceAdapter implements ITechnologyServicePort {

    private final WebClient webClient;

    public TechnologyServiceAdapter(WebClient.Builder webClientBuilder,
                                    @Value("${external.services.technology.base-url}") String baseUrl,
                                    @Value("${flags.webclient-builder-debug}") Boolean webClientBuilderDebug) {
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .codecs(configurer -> configurer.defaultCodecs().enableLoggingRequestDetails(webClientBuilderDebug)) /*borrar*/
                .build();
    }

    @Override
    public Mono<Boolean> existsById(List<String> techIds) {
        String techIdsAsString = String.join(", ", techIds);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/exists")
                        .queryParam("techIds", techIdsAsString)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e ->
                        log.error("Error al verificar la existencia de las tecnologías con ids {}: {}"
                                , techIdsAsString, e.getMessage()))
                .onErrorResume(e ->
                        Mono.error(new InconsistencyDataException(
                                        String.format("Error al verificar la existencia de las tecnologías con ids %s, mensaje: %s"
                                                , techIdsAsString, e.getMessage())))
                );
    }
}
