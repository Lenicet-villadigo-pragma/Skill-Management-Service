package reactivechallenge.pragma.skillmanagementservice.usecase;

import reactivechallenge.pragma.skillmanagementservice.api.IRetrieveSkillsServicePort;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillPaginationResult;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ListSkillUseCase implements IRetrieveSkillsServicePort {

    private final ISkillRepositoryPort skillRepositoryPort;

    public ListSkillUseCase(ISkillRepositoryPort skillRepositoryPort) {
        this.skillRepositoryPort = skillRepositoryPort;
    }

    @Override
    public Mono<SkillPaginationResult<SkillModel>> retrieveSkills(SkillSortField skillSortField
            , SkillSortOrder skillSortOrder, Integer pageNumber, Integer pageSize) {

        return Mono.zip(
                skillRepositoryPort.getSkills(
                        skillSortField == null? SkillSortField.NAME:skillSortField
                        , skillSortOrder ==null?SkillSortOrder.ASC:skillSortOrder
                        , pageNumber,pageSize)
                        .collectList()
                , skillRepositoryPort.countSkills()
        ).map(tuple -> new SkillPaginationResult<>(tuple.getT1(), tuple.getT2()));
        // tuple.getT1() -> es la lista de eskills obtenida en el metodo .zip
        // tuple.getT2() -> es la cantidad de skills obtenido en el metodo .zip
    }
    
    @Override
    public Mono<Boolean> verifyIfExists(List<Long> ids) {
        return skillRepositoryPort.exists(ids);
    }

    @Override
    public List<Long> verifySkillIds(String skillIdsAsString){
        Optional<String> skillIdsAsStringOpt = Optional.ofNullable(skillIdsAsString);
        List<String> skillIds = skillIdsAsStringOpt
                .map(idList -> Arrays.stream(idList.split(","))
                        .map(idString -> idString.replaceAll("[\"/\\\\]", "").trim())
                        .toList())
                .orElse(Collections.emptyList());

        if(skillIds.isEmpty()){
            throw new IllegalArgumentException("No se proporcionaron IDs de sills. Asegúrate de incluir el " +
                    "parámetro 'skillIds' con al menos un ID.");
        }
        if(skillIds.stream().anyMatch(id -> !id.matches("\\d+"))){
            throw new IllegalArgumentException("Formato de IDs inválido. Todos los IDs deben ser números.");
        }

        return skillIds.stream().map(Long::valueOf).toList();
    }
}
