package reactivechallenge.pragma.skillmanagementservice.usecase;

import lombok.extern.slf4j.Slf4j;
import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactivechallenge.pragma.skillmanagementservice.exception.InconsistencyDataException;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
public class CreateSkillUseCase implements IRegisterSkillServicePort {
    private final ISkillRepositoryPort skillRepository;
    private final ITechnologyServicePort technologyServicePort;

    public CreateSkillUseCase(ISkillRepositoryPort skillRepository, ITechnologyServicePort technologyServicePort) {
        this.technologyServicePort = technologyServicePort;
        this.skillRepository = skillRepository;
    }

    @Override
    public Mono<SkillModel> registerSkill(SkillModel skillModel) {
        return technologyServicePort.existsById(skillModel.getTechnologyIdsAsString())
                .doOnNext(exists -> log.info("Exiten las tecnologías enviasdas? {}", exists))
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        log.error("La tecnología con id: {} no existe", skillModel.getTechnologyIdsAsString());
                        return Mono.error(new InconsistencyDataException(
                                String.format("La tecnología con id: %s no existe", skillModel.getTechnologyIdsAsString())
                        ));
                    }
                    return skillRepository.save(skillModel);
                });
    }


}
