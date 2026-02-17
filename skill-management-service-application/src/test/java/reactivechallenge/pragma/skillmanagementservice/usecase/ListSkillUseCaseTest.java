package reactivechallenge.pragma.skillmanagementservice.usecase;

import org.junit.jupiter.api.DisplayName;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

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

    @Test
    @DisplayName("Verify if exists returns true when skill exists")
    void verifyIfExistsReturnsTrue() {
        // Arrange
        List<Long> skillsIds = List.of(1L);
        when(skillRepositoryPort.exists(skillsIds)).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = listSkillUseCase.verifyIfExists(skillsIds);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(skillRepositoryPort).exists(skillsIds);
    }

    @Test
    @DisplayName("Verify if exists returns false when skill does not exist")
    void verifyIfExistsReturnsFalse() {
        // Arrange
        List<Long> skillsIds = List.of(999L);
        when(skillRepositoryPort.exists(skillsIds)).thenReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = listSkillUseCase.verifyIfExists(skillsIds);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(skillRepositoryPort).exists(skillsIds);
    }

    @Test
    @DisplayName("Verify if exists propagates error from repository")
    void verifyIfExistsPropagatesError() {
        // Arrange
        List<Long> skillsIds = List.of(1L);
        RuntimeException expectedException = new RuntimeException("Database error");
        when(skillRepositoryPort.exists(skillsIds)).thenReturn(Mono.error(expectedException));

        // Act
        Mono<Boolean> result = listSkillUseCase.verifyIfExists(skillsIds);

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(skillRepositoryPort).exists(skillsIds);
    }

    @Test
    @DisplayName("Verify if exists handles null id gracefully")
    void verifyIfExistsHandlesNullId() {
        // Arrange
        when(skillRepositoryPort.exists(null)).thenReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = listSkillUseCase.verifyIfExists(null);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(skillRepositoryPort).exists(null);
    }

    @Test
    @DisplayName("verifySkillsIds retorna lista de Longs a partir de string de ids separados por comas")
    void verifySkillsIdsReturnsListOfLongs() {
        // Arrange
        String techIdsAsString = "1, 2, 3";

        // Act
        List<Long> result = listSkillUseCase.verifySkillIds(techIdsAsString);

        // Assert
        assert result.equals(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("verifySkillsIds lanza IllegalArgumentException cuando se pasa un string nulo")
    void verifySkillsIdsThrowExeptionWhenNullString() {
        // Arrange
        RuntimeException exceptionExpected = new IllegalArgumentException("No se proporcionaron IDs de sills. Asegúrate de incluir el " +
                "parámetro 'skillIds' con al menos un ID.");
        RuntimeException exceptionObtained=null;

        // Act
        try {
            listSkillUseCase.verifySkillIds(null);
        } catch (IllegalArgumentException e) {
            exceptionObtained = e;
        }

        // Assert
        assert exceptionObtained!=null && exceptionObtained.getMessage().equals(exceptionExpected.getMessage());
    }

    @Test
    @DisplayName("verifySkillsIds lanza IllegalArgumentException cuando se pasa un string de letras separados por coma")
    void verifySkillsIdsThrowExeptionWhenStringIsNotNumbers() {
        // Arrange
        String ids = "a, b, c";
        RuntimeException exceptionExpected = new IllegalArgumentException("Formato de IDs inválido. Todos los IDs deben ser números.");
        RuntimeException exceptionObtained=null;

        // Act
        try {
            listSkillUseCase.verifySkillIds(ids);
        } catch (IllegalArgumentException e) {
            exceptionObtained = e;
        }

        // Assert
        assert exceptionObtained!=null && exceptionObtained.getMessage().equals(exceptionExpected.getMessage());
    }

    @Test
    @DisplayName("getSkillsByIds returns list of technologies when they exist")
    void getSkillsByIdsReturnsList() {
        // Arrange
        List<TechnologyExternalModel> externalModelList = List.of(new TechnologyExternalModel(1L, "test")
                , new TechnologyExternalModel(3L, "test"), new TechnologyExternalModel(2L, "test"));
        List<Long> skillsIds = List.of(1L);
        SkillModel skillModel=new SkillModel(null, "Java", "Programming language", externalModelList);
        when(skillRepositoryPort.getSkillsById(skillsIds)).thenReturn(Flux.just(skillModel));

        // Act
        Flux<SkillModel> result = listSkillUseCase.getSkillsByIds(skillsIds);

        // Assert
        StepVerifier.create(result)
                .expectNext(skillModel)
                .verifyComplete();

        verify(skillRepositoryPort).getSkillsById(skillsIds);
    }

    @Test
    @DisplayName("getSkillsByIds returns empty when list of ids is empty")
    void getSkillsByIdsReturnsEmpty() {
        // Arrange
        List<Long> skillsIds = new ArrayList<>();


        // Act
        Flux<SkillModel> result =listSkillUseCase.getSkillsByIds(skillsIds);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(skillRepositoryPort, never()).getSkillsById(skillsIds);
    }

    @Test
    @DisplayName("getSkillsByIds returns empty when list of ids is null")
    void getSkillsByIdsReturnsEmpty2() {
        // Act
        Flux<SkillModel> result = listSkillUseCase.getSkillsByIds(null);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(skillRepositoryPort, never()).getSkillsById(null);
    }
}
