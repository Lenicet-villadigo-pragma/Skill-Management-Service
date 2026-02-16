package reactivechallenge.pragma.skillmanagementservice.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillPaginationResult;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ListSkillUseCaseTest {

    @Mock
    ISkillRepositoryPort skillRepositoryPort;

    @InjectMocks
    ListSkillUseCase listSkillUseCase;

    @Test
    void retrieveSkillsResponseSuccessful(){

        // Arrange
        List<TechnologyExternalModel> externalModelList = List.of(new TechnologyExternalModel(1L, "test")
        , new TechnologyExternalModel(3L, "test"), new TechnologyExternalModel(2L, "test"));
        SkillModel skillModel = new SkillModel(1L, "test","description",externalModelList);
        Flux<SkillModel> fluxSkillModel = Flux.just(skillModel);
        Mono<Long> totalTechnologies = Mono.just(1L);
        SkillPaginationResult<SkillModel> responseExpected = new SkillPaginationResult<>(List.of(skillModel),1L);

        Mockito.when(skillRepositoryPort.getSkills(Mockito.any(SkillSortField.class), Mockito.any(SkillSortOrder.class)
        , Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(fluxSkillModel);
        Mockito.when(skillRepositoryPort.countSkills()).thenReturn(totalTechnologies);

        //Act
        Mono<SkillPaginationResult<SkillModel>> response = listSkillUseCase.retrieveSkills(SkillSortField.NAME
                , SkillSortOrder.ASC, 0, 0);

        // Assert
        StepVerifier.create(response)
                .expectNext(responseExpected)
                .verifyComplete();

        Mockito.verify(skillRepositoryPort).getSkills(Mockito.any(SkillSortField.class), Mockito.any(SkillSortOrder.class)
                , Mockito.any(Integer.class), Mockito.any(Integer.class));
        Mockito.verify(skillRepositoryPort).countSkills();
    }
}
