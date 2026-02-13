package reactivechallenge.pragma.skillmanagementservice.input.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivechallenge.pragma.skillmanagementservice.input.dto.SkillCreateDto;
import reactivechallenge.pragma.skillmanagementservice.input.handler.SkillHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SkillRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/create",
                    produces = {
                            MediaType.APPLICATION_JSON_VALUE
                    },
                    method = RequestMethod.POST,
                    beanClass = SkillHandler.class,
                    beanMethod = "createSkill",
                    operation = @Operation(
                            operationId = "createSkill",
                            summary = "Crear nueva capacidad",
                            description = "Crea un nuevo registro de capacidad en el sistema con la información proporcionada.",
                            tags = {"Gestión de Capacidades"},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Skill created successfully",
                                            content = @Content(schema = @Schema(implementation = SkillCreateDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid Input"
                                    )
                            },
                            requestBody = @RequestBody(
                                    content = @Content(schema = @Schema(implementation = SkillCreateDto.class))
                            )
                    )
            )
    })
    public RouterFunction<ServerResponse> skillRoutes(SkillHandler skillHandler) {
        return route(POST("/create").and(accept(MediaType.APPLICATION_JSON)), skillHandler::createSkill);
    }
}
