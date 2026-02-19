package reactivechallenge.pragma.skillmanagementservice.usecase;

import lombok.extern.slf4j.Slf4j;
import reactivechallenge.pragma.skillmanagementservice.api.IDeleteSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class DeleteSkillUseCase implements IDeleteSkillServicePort {

    private final ISkillRepositoryPort skillRepositoryPort;
    private final ITechnologyServicePort technologyServicePort;

    public DeleteSkillUseCase(ISkillRepositoryPort skillRepositoryPort,  ITechnologyServicePort technologyServicePort) {
        this.skillRepositoryPort = skillRepositoryPort;
        this.technologyServicePort = technologyServicePort;
    }

    @Override
    public Mono<Void> deleteSkillsByIds(List<Long> skillIds) {
        if(skillIds==null){
            return Mono.empty();
        }
        return Flux.fromIterable(skillIds)
                .flatMap(skillRepositoryPort::getTechIdsBySkillId)
                .collectList()
                .map(techsIds -> techsIds.stream().distinct().toList())
                .flatMap(affectedTechIds ->
                        // 2. OPERACIÓN LOCAL ATÓMICA
                        // Procesamos cada capacidad para borrar sus relaciones y luego las capacidades mismas
                        Flux.fromIterable(skillIds)
                                .flatMap(skillId ->
                                        skillRepositoryPort.deleteSkillTechnologiesRelation(skillId)
                                                .thenReturn(skillIds)
                                )
                                .flatMap(skillRepositoryPort::deleteSkillByIds)
                                .then() // Esperamos a que todas las capacidades locales se borren

                                // 3. LIMPIEZA EXTERNA (Transparente y posterior al éxito local)
                                .then(
                                        Flux.fromIterable(affectedTechIds)
                                                .flatMap(techId ->
                                                        skillRepositoryPort.getTotalTechRelationWithSkills(techId)
                                                                .filter(totalRelations -> totalRelations == 0)
                                                                .flatMap(techWithoutRelations -> technologyServicePort.deleteTechById(String.valueOf(techId)))
                                                                .onErrorResume(e -> {
                                                                    log.error("No se pudo eliminar las tecnologías sueltas, error {}", e.getMessage());
                                                                    return Mono.empty();
                                                                })
                                                )
                                                .then()
                                )
                );
    }

}
