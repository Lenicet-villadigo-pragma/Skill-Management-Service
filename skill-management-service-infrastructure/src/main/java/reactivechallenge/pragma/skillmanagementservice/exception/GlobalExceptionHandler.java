package reactivechallenge.pragma.skillmanagementservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = determineStatus(ex);
        log.error("Error : {}", ex.getLocalizedMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return writeResponse(exchange, status, errorResponse);
    }

    private HttpStatus determineStatus(Throwable ex) {
        return switch (ex) {
            case BusinessDomainException e -> HttpStatus.BAD_REQUEST;
            case IllegalArgumentException e-> HttpStatus.BAD_REQUEST;
            case InconsistencyDataException e-> HttpStatus.CONFLICT;
            case GenericDatabaseException e ->HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, ErrorResponse errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        try {
            DataBuffer buffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }
}
