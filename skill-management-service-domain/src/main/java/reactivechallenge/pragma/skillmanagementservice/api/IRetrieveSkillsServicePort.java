package reactivechallenge.pragma.skillmanagementservice.api;

import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillPaginationResult;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IRetrieveSkillsServicePort {
    Mono<SkillPaginationResult<SkillModel>> retrieveSkills(SkillSortField skillSortField, SkillSortOrder skillSortOrder
            , Integer pageNumber, Integer pageSize);
    Mono<Boolean> verifyIfExists(List<Long> ids);
    List<Long> verifySkillIds(String skillIdsAsString);
}
