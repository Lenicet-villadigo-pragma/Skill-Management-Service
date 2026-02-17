package reactivechallenge.pragma.skillmanagementservice.input.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.api.IRetrieveSkillsServicePort;
import reactivechallenge.pragma.skillmanagementservice.input.dto.*;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class SkillHandler {

    private final IRegisterSkillServicePort registerSkillServicePort;
    private final IRetrieveSkillsServicePort retrieveSkillsServicePort;
    private final ITechnologyServicePort technologyServicePort;
    private final int sortPageNumberDefault;
    private final int sortPageSizeDefault;

    public SkillHandler(IRegisterSkillServicePort registerSkillServicePort,
                        IRetrieveSkillsServicePort retrieveSkillsServicePort, ITechnologyServicePort technologyServicePort
    , @Value("${app.pagination.default-page}") int sortPageNumberDefault
    , @Value("${app.pagination.default-size}") int sortPageSizeDefault) {
        this.registerSkillServicePort = registerSkillServicePort;
        this.retrieveSkillsServicePort = retrieveSkillsServicePort;
        this.technologyServicePort = technologyServicePort;
        this.sortPageNumberDefault = sortPageNumberDefault;
        this.sortPageSizeDefault = sortPageSizeDefault;
    }

    public Mono<ServerResponse> createSkill(ServerRequest request) {
        return request.bodyToMono(SkillCreateDto.class)
                .map(SkillCreateDto::toModel)
                .flatMap(registerSkillServicePort::registerSkill)
                .map(SkillCreateDto::fromModel)
                .flatMap(skillDto -> ServerResponse.created(request.uri()) // O simplemente .ok() dependiendo de si quiero devolver la URI
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(skillDto));
    }

    public Mono<ServerResponse> listSkills(ServerRequest request){
        SkillSortField skillSortField = getSKillSortField(request.pathVariable("sortField"));
        SkillSortOrder skillSortOrder = getSKillSortOrder(request.pathVariable("sortOrder"));
        Integer pageNumber = getValidateNumber(request.pathVariable("pageNumber"), sortPageNumberDefault);
        Integer pageSize = getValidateNumber(request.pathVariable("pageSize"), sortPageSizeDefault);


        return retrieveSkillsServicePort
                .retrieveSkills(skillSortField, skillSortOrder, pageNumber, pageSize)
                .flatMap(skillPaginationResultModel ->
                    Flux.fromIterable(skillPaginationResultModel.items())
                            .concatMap(skillModel ->
                                        technologyServicePort.getTechsByIds(skillModel.getTechnologyIdsAsString())
                                               .map(TechResponseForListDto::fromModel)
                                               .collectList()
                                               .map(listTechsDto ->
                                                    new ListSkillResponseDto(skillModel.id(), skillModel.name(), listTechsDto)
                                               )
                            )
                            .collectList()
                            .flatMap(listSkillResponseDto ->{
                                SkillPaginatedDto<ListSkillResponseDto> response = new SkillPaginatedDto<>(
                                        listSkillResponseDto, skillPaginationResultModel.total(),pageNumber, pageSize
                                );

                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(response);
                            })
                );
    }

    private SkillSortField getSKillSortField (String valueForSortField) {
       SkillSortField skillSortField = SkillSortField.fromString(valueForSortField);
       log.info("el valor para ordenación encontrado es {}", skillSortField.getFieldName());
       return skillSortField;
    }

    private SkillSortOrder getSKillSortOrder (String valueForSortOrder) {
        SkillSortOrder skillSortOrder = SkillSortOrder.fromString(valueForSortOrder);
        log.info("el sentido para ordenación encontrado es {}", skillSortOrder.getSortOrder());
        return skillSortOrder;
    }

    private Integer getValidateNumber(String numberAsString, int defaultValue){
        int number = defaultValue;
        if(numberAsString!=null){
            try {
               number = Integer.parseInt(numberAsString.toLowerCase().replaceAll("\\s+", " ").trim());
            } catch (Exception e) {
                log.error("el valor enviado {} no es válido como numero natural", numberAsString);
            }
        }
        return number;
    }

    public Mono<ServerResponse> verifyIfSkillsExists(ServerRequest request) {
        return Mono.just(getSkillIdsFromRequest(request))
                .flatMap(retrieveSkillsServicePort::verifyIfExists)
                .flatMap(exists -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(exists))
                .onErrorResume(NumberFormatException.class,
                        e -> ServerResponse.badRequest()
                                .bodyValue("Formato de IDs inválido. Deben ser números."));
    }

    private List<Long> getSkillIdsFromRequest(ServerRequest request) {
        Optional<String> stringSkillIds =  request.queryParam("skillIds");
        return retrieveSkillsServicePort.verifySkillIds(stringSkillIds.orElse(null));
    }
}
