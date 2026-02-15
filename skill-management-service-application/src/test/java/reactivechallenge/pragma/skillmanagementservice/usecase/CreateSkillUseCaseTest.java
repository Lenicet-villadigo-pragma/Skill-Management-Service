package reactivechallenge.pragma.skillmanagementservice.usecase;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactivechallenge.pragma.skillmanagementservice.exception.InconsistencyDataException;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CreateSkillUseCaseTest {


    @Mock
    ISkillRepositoryPort skillRepositoryPort;

    @Mock
    ITechnologyServicePort technologyServicePort;

    @InjectMocks
    CreateSkillUseCase createSkillUseCase;

    @Test
    @DisplayName("Create technology successfully")
    void createSkillSuccess() {
        // Arrange
        List<TechnologyExternalModel> technologyExternalModelList = List.of(new TechnologyExternalModel(1L)
                ,new TechnologyExternalModel(2L), new TechnologyExternalModel(3L));
        SkillModel inputModel = new SkillModel(null, "  JAVA  Programming  ", "Description"
                ,technologyExternalModelList);
        SkillModel returnedModel = new SkillModel(1L, "java programming", "Description"
                ,technologyExternalModelList);

        Mockito.when(technologyServicePort.existsById(Mockito.anyList())).thenReturn(Mono.just(true));
        Mockito.when(skillRepositoryPort.save(Mockito.any(SkillModel.class))).thenReturn(Mono.just(returnedModel));

        // Act
        Mono<SkillModel> result = createSkillUseCase.registerSkill(inputModel);

        // Assert
        StepVerifier.create(result)
                .expectNext(returnedModel)
                .verifyComplete();

        Mockito.verify(technologyServicePort).existsById(Mockito.anyList());
        Mockito.verify(skillRepositoryPort).save(inputModel);
    }

    @Test
    @DisplayName("Create technology Throw InconsistencyDataException because tech ids does not exists")
    void createSkillThrowInconsistencyDataException() {
        // Arrange
        List<TechnologyExternalModel> technologyExternalModelList = List.of(new TechnologyExternalModel(1L)
                ,new TechnologyExternalModel(2L), new TechnologyExternalModel(3L));
        SkillModel inputModel = new SkillModel(null, "  JAVA  Programming  ", "Description"
                ,technologyExternalModelList);

        Mockito.when(technologyServicePort.existsById(Mockito.anyList())).thenReturn(Mono.just(false));

        // Act
        Mono<SkillModel> result = createSkillUseCase.registerSkill(inputModel);

        // Assert
        StepVerifier.create(result).expectErrorSatisfies(throwable -> {
            Assertions.assertThat(throwable).isInstanceOf(InconsistencyDataException.class);
            Assertions.assertThat(throwable.getMessage()).isEqualTo("La tecnología con id: [1, 2, 3] no existe");
        }).verify();

        Mockito.verify(technologyServicePort).existsById(Mockito.anyList());
        Mockito.verify(skillRepositoryPort, Mockito.never()).save(inputModel);
    }

}
