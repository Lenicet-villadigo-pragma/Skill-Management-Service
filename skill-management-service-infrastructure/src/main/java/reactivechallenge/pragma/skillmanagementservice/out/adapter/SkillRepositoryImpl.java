package reactivechallenge.pragma.skillmanagementservice.out.adapter;

import org.springframework.stereotype.Component;
import reactivechallenge.pragma.skillmanagementservice.mapper.DatabaseErrorMapper;
import reactivechallenge.pragma.skillmanagementservice.mapper.SkillEntityMapper;
import reactivechallenge.pragma.skillmanagementservice.mapper.SkillTechnologyEntityMapper;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillTechnologyEntity;
import reactivechallenge.pragma.skillmanagementservice.out.repository.ISkillRepository;
import reactivechallenge.pragma.skillmanagementservice.out.repository.ISkillTechnologyRepository;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
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
                .flatMap(savedSkillEntity -> saveSkillTechnologies(skillEntityMapper.toModel(savedSkillEntity, skillModel.technologies())))
                .onErrorMap(databaseErrorMapper::map);
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

    @Override
    public Mono<SkillModel> findById(Long id) {
        return null;
    }

    @Override
    public Flux<SkillModel> findAll() {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return null;
    }

    @Override
    public Mono<Void> delete(SkillModel entity) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return null;
    }

    @Override
    public Mono<Long> count() {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }
}
