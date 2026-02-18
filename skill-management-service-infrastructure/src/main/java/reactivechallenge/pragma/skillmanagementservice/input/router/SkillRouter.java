package reactivechallenge.pragma.skillmanagementservice.input.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import reactivechallenge.pragma.skillmanagementservice.input.dto.SkillPaginatedDto;
import reactivechallenge.pragma.skillmanagementservice.input.handler.SkillHandler;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
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
            ,@RouterOperation(
            path = "/retrieve/sortField/{sortField}/sortOrder/{sortOrder}/pageSize/{pageSize}/pageNumber/{pageNumber}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET,
            beanClass = SkillHandler.class,
            beanMethod = "listSkills",
            operation = @Operation(
                    operationId = "listSkills",
                    summary = "Listar capacidades existentes",
                    description = "Se obtienen las capacidades con sus respectivas tecnologías, " +
                            "se puede ordenar ascendente (asc) o descendente (desc) ya sea por nombre o " +
                            "cantidad de tecnologías asociadas",
                    tags = {"Gestión de Capacidades"},
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Ok",
                                    content = @Content(schema = @Schema(implementation = SkillPaginatedDto.class))
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Invalid Input"
                            )
                    },
                    parameters = {
                            @Parameter(in = ParameterIn.QUERY, name = "sortField", description = "campo opcional para ordenar, valore: name o total_technologies", schema = @Schema(implementation = SkillSortField.class))
                            ,@Parameter(in = ParameterIn.QUERY, name = "sortOrder", description = "Sentido de ordenación, opcional. valores: asc o desc", schema = @Schema(implementation = SkillSortOrder.class))
                            ,@Parameter(in = ParameterIn.QUERY, name = "pageNumber", description = "Número de página, opcional", schema = @Schema(implementation = Integer.class))
                            ,@Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Cantidad de registros por página, opcional", schema = @Schema(implementation = Integer.class))

                    })
            )
            ,@RouterOperation(
            path = "/exists",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            method = RequestMethod.GET,
            beanClass = SkillHandler.class,
            beanMethod = "verifyIfSkillsExists",
            operation = @Operation(
                    operationId = "verifyIfSkillsExists",
                    summary = "Verificar existencia de una o más capacidades",
                    description = "Consulta si las capacidades enviadas existen en la base de datos.",
                    tags = {"Gestión de Capacidades"},
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Skill existence verified",
                                    content = @Content(schema = @Schema(implementation = Boolean.class))
                            )
                    },
                    parameters = {
                            @Parameter(in = ParameterIn.QUERY, name = "skillIds", description = "list of skill IDs")
                    })
            )
    })
    public RouterFunction<ServerResponse> skillRoutes(SkillHandler skillHandler) {
        return route(POST("/create").and(accept(MediaType.APPLICATION_JSON)), skillHandler::createSkill)
                .andRoute(GET("/retrieve"), skillHandler::listSkills)
                .andRoute(GET("/exists"), skillHandler::verifyIfSkillsExists);
    }
}
