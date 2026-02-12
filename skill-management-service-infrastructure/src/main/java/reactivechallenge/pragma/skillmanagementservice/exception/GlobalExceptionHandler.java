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
import org.springframework.web.server.ServerWebInputException;
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
                getErrorMessage(ex),
                LocalDateTime.now()
        );

        return writeResponse(exchange, status, errorResponse);
    }

    private String getErrorMessage(Throwable ex) {
        String message = ex.getMessage() == null || ex.getMessage().isEmpty() ? "An unexpected error occurred" :  ex.getMessage();
        if(ex instanceof ServerWebInputException e){
            try{
                String reason = e.getReason();
                String parameter = (e.getMethodParameter() != null
                        && e.getMethodParameter().getParameterName() != null)
                        ? e.getMethodParameter().getParameterName() : "desconocido";
                message = String.format("Error de entrada en el parámetro [%s]. Razón: %s", parameter, reason);
            } catch (Exception exception){
                log.error("No se pudo obtener el error detallado de ServerWebInputException");
            }

        }
        return message;
    }

    private HttpStatus determineStatus(Throwable ex) {
        return switch (ex) {
            case BusinessDomainException e -> HttpStatus.BAD_REQUEST;
            case IllegalArgumentException e-> HttpStatus.BAD_REQUEST;
            case InconsistencyDataException e-> HttpStatus.CONFLICT;
            case GenericDatabaseException e ->HttpStatus.CONFLICT;
            case ServerWebInputException e -> HttpStatus.BAD_REQUEST;
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
