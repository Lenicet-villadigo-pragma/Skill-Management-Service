package reactivechallenge.pragma.skillmanagementservice.usecase;

import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactivechallenge.pragma.skillmanagementservice.exception.InconsistencyDataException;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CreateSkillUseCase implements IRegisterSkillServicePort {
    private final ISkillRepositoryPort skillRepository;
    private final ITechnologyServicePort technologyServicePort;

    public CreateSkillUseCase(ISkillRepositoryPort skillRepository, ITechnologyServicePort technologyServicePort) {
        this.technologyServicePort = technologyServicePort;
        this.skillRepository = skillRepository;
    }

    @Override
    public Mono<SkillModel> registerSkill(SkillModel skillModel) {
        return Flux.fromIterable(skillModel.technologies())
                .flatMap(tech -> technologyServicePort.existsById(tech.id())
                        .filter(exists -> exists)
                        .switchIfEmpty(Mono.error(new InconsistencyDataException(String.format("La tecnologia con id %d no existe", tech.id()))))
                )
                .then(skillRepository.save(skillModel));
    }


}
