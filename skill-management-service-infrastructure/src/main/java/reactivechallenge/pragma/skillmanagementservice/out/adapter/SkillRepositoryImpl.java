package reactivechallenge.pragma.skillmanagementservice.out.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;
import reactivechallenge.pragma.skillmanagementservice.mapper.DatabaseErrorMapper;
import reactivechallenge.pragma.skillmanagementservice.mapper.SkillEntityMapper;
import reactivechallenge.pragma.skillmanagementservice.mapper.SkillTechnologyEntityMapper;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillEntity;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillTechnologyEntity;
import reactivechallenge.pragma.skillmanagementservice.out.repository.ISkillRepository;
import reactivechallenge.pragma.skillmanagementservice.out.repository.ISkillTechnologyRepository;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public record SkillRepositoryImpl(
        ISkillRepository skillRepository,
        ISkillTechnologyRepository skillTechnologyRepository,
        SkillEntityMapper skillEntityMapper,
        DatabaseErrorMapper databaseErrorMapper,
        SkillTechnologyEntityMapper skillTechnologyEntityMapper
) implements ISkillRepositoryPort {

    @Override
    public Mono<SkillModel> save(SkillModel skillModel) {
        return skillRepository.save(skillEntityMapper.toEntity(skillModel))
                .flatMap(savedSkillEntity -> saveSkillTechnologies(skillEntityMapper.toModel(savedSkillEntity,
                        skillModel.technologies())))
                .onErrorMap(databaseErrorMapper::map);
    }

    @Override
    public Flux<SkillModel> getSkills(SkillSortField skillSortField, SkillSortOrder skillSortOrder
            , Integer pageNumber, Integer pageSize) {

        Sort sort = Sort.by(Sort.Direction.fromString(skillSortOrder.getSortOrder()), skillSortField.getFieldName());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        return getSkillModelFlux(skillRepository.findAllBy(pageable));
    }

    @Override
    public Mono<Long> countSkills() {
        return skillRepository.count();
    }

    @Override
    public Mono<Boolean> exists(List<Long> ids) {
        return skillRepository.findAllById(ids)
                .collectList()
                .map(listTechs -> listTechs.size() == ids.size())
                .onErrorMap(databaseErrorMapper::map);
    }

    @Override
    public Flux<SkillModel> getSkillsById(List<Long> ids) {
        return getSkillModelFlux(skillRepository.findAllById(ids));
    }

    private Mono<SkillModel> saveSkillTechnologies(SkillModel skillModel) {
        List<TechnologyExternalModel> technologies = skillModel.technologies();

        if (technologies == null || technologies.isEmpty()) {
            return Mono.just(skillModel);
        }

        List<SkillTechnologyEntity> skillTechnologyEntities = technologies.stream()
                .map(tech -> skillTechnologyEntityMapper.toEntity(skillModel, tech.id()))
                .toList();

        return skillTechnologyRepository.saveAll(skillTechnologyEntities)
                .then(Mono.just(skillModel));
    }

    private Flux<TechnologyExternalModel> getTechsBySkillId(Long skillId){
       return skillTechnologyRepository.findAllBySkillId(skillId)
               .map(skillTechnologyEntityMapper::toTechnologyExternalModel);
    }

    private Flux<SkillModel> getSkillModelFlux(Flux<SkillEntity> skillEntityFlux){
        return skillEntityFlux.concatMap(skillEntity ->
                getTechsBySkillId(skillEntity.id()).collectList()
                        .flatMap(techList ->{
                            if (techList.isEmpty()) {
                                return Mono.error(new BusinessDomainException("Skill sin tecnologías"));
                            }
                            return Mono.just(skillEntityMapper.toModel(skillEntity, techList));
                        }).onErrorResume(error -> {
                            log.warn("Omitiendo skill {} por error: {}", skillEntity.id(), error.getMessage());
                            return Mono.empty();
                        })
        );
    }
}
