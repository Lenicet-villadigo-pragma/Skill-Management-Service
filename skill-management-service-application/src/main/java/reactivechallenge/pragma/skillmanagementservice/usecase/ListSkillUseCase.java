package reactivechallenge.pragma.skillmanagementservice.usecase;

import reactivechallenge.pragma.skillmanagementservice.api.IRetrieveSkillsServicePort;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillPaginationResult;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Mono;

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
}
