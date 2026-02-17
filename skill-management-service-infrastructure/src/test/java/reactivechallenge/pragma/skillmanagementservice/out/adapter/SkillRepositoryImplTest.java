package reactivechallenge.pragma.skillmanagementservice.out.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactivechallenge.pragma.skillmanagementservice.exception.GenericDatabaseException;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillRepositoryImplTest {

    @Mock
    ISkillRepository skillRepositoryMock;
    @Mock
    ISkillTechnologyRepository skillTechnologyRepositoryMock;

     private SkillRepositoryImpl skillRepositoryImpl;

     @BeforeEach
     void setup() {
            DatabaseErrorMapper errorMapper = new DatabaseErrorMapper();
            SkillEntityMapper skillEntityMapper = new SkillEntityMapper();
            SkillTechnologyEntityMapper skillTechnologyEntityMapper = new SkillTechnologyEntityMapper();
            skillRepositoryImpl = new SkillRepositoryImpl(skillRepositoryMock, skillTechnologyRepositoryMock, skillEntityMapper, errorMapper, skillTechnologyEntityMapper);
     }

     @Test
     @DisplayName("Save skill successfully")
     void saveSkillSuccess() {
         // Arrange
         List<TechnologyExternalModel> technologies = List.of(new TechnologyExternalModel(1L,""),
                 new TechnologyExternalModel(21L,""), new TechnologyExternalModel(3L,""));
         SkillEntity skillEntityToBeSaved = new SkillEntity(null, "Java", "Description", technologies.size());
         SkillEntity skillEntitySaved = new SkillEntity(1L, "Java", "Description", technologies.size());
         SkillModel skillModelToBeSaved= new SkillModel(null, "Java", "Description", technologies);
         SkillModel skillModelSaved = new SkillModel(1L, "Java", "Description", technologies);
         List<SkillTechnologyEntity> skillTechEntityList = List.of(new SkillTechnologyEntity(1L, 1L));

         when(skillRepositoryMock.save(any(SkillEntity.class))).thenReturn(Mono.just(skillEntitySaved));
         when(skillTechnologyRepositoryMock.saveAll(anyList())).thenReturn(Flux.fromIterable(skillTechEntityList));

         // Act
            Mono<SkillModel> result = skillRepositoryImpl.save(skillModelToBeSaved);

         //Assert
         StepVerifier.create(result)
                 .expectNext(skillModelSaved)
                 .verifyComplete();
         verify(skillRepositoryMock).save(skillEntityToBeSaved);
     }

     @Test
     @DisplayName("get skills goes well")
    void getSkillSuccess(){
         // Arrange
         Flux<SkillTechnologyEntity> fluxSkillTechnologyEntities = Flux.just(
                 new SkillTechnologyEntity(1L,1L)
                 , new SkillTechnologyEntity(2L,2L)
                 , new SkillTechnologyEntity(3L,3L)
         );
         List<TechnologyExternalModel> technologyExternalModelList = List.of(
                 new TechnologyExternalModel(1L,"")
                 , new TechnologyExternalModel(2L,"")
                 , new TechnologyExternalModel(3L,"")
         );
         SkillEntity skillEntity = new SkillEntity(1L,"name", "description", 3);
         SkillModel skillModel = new SkillModel(1L,"name", "description", technologyExternalModelList);
         Flux<SkillEntity> skillEntityFlux = Flux.just(skillEntity);

         when(skillRepositoryMock.findAllBy(any(Pageable.class))).thenReturn(skillEntityFlux);
         when(skillTechnologyRepositoryMock.findAllBySkillId(anyLong())).thenReturn(fluxSkillTechnologyEntities);

         // Act
         Flux<SkillModel> response = skillRepositoryImpl.getSkills(SkillSortField.NAME, SkillSortOrder.ASC,0,10);

         // Assert
         StepVerifier.create(response)
                 .expectNext(skillModel)
                 .verifyComplete();
         verify(skillRepositoryMock).findAllBy(any(Pageable.class));
         verify(skillTechnologyRepositoryMock).findAllBySkillId(anyLong());
     }

    @Test
    @DisplayName("get skills goes well even when one of the skill have not list of skill")
    void getSkillSuccessPartially(){
        // Arrange
        Flux<SkillTechnologyEntity> fluxSkillTechnologyEntities = Flux.just(
                new SkillTechnologyEntity(1L,1L)
                , new SkillTechnologyEntity(2L,2L)
                , new SkillTechnologyEntity(3L,3L)
        );
        List<TechnologyExternalModel> technologyExternalModelList = List.of(
                new TechnologyExternalModel(1L,"")
                , new TechnologyExternalModel(2L,"")
                , new TechnologyExternalModel(3L,"")
        );
        SkillEntity skillEntity1 = new SkillEntity(1L,"name", "description", 3);
        SkillEntity skillEntity2 = new SkillEntity(2L,"name2", "description2", 0);
        SkillModel skillModel = new SkillModel(1L,"name", "description", technologyExternalModelList);
        Flux<SkillEntity> skillEntityFlux = Flux.just(skillEntity1, skillEntity2);

        when(skillRepositoryMock.findAllBy(any(Pageable.class))).thenReturn(skillEntityFlux);
        when(skillTechnologyRepositoryMock.findAllBySkillId(anyLong())).thenReturn(fluxSkillTechnologyEntities).thenReturn(Flux.empty());

        // Act
        Flux<SkillModel> response = skillRepositoryImpl.getSkills(SkillSortField.NAME, SkillSortOrder.ASC,0,10);

        // Assert
        StepVerifier.create(response)
                .expectNext(skillModel)
                .verifyComplete();
        verify(skillRepositoryMock).findAllBy(any(Pageable.class));
        verify(skillTechnologyRepositoryMock, times(2)).findAllBySkillId(anyLong());
    }

    @Test
    @DisplayName("countSkills returns correctly")
    void countSkillsTest() {
         // Arrange
        Mono<Long> actual;
       Long expected = 1L;

       when(skillRepositoryMock.count()).thenReturn(Mono.just(1L));

        // Arrange
        actual = skillRepositoryImpl.countSkills();

        // Assert
        StepVerifier.create(actual).expectNext(expected).verifyComplete();
        verify(skillRepositoryMock).count();
    }

    @Test
    @DisplayName("Exists returns true when technology exists")
    void existsReturnsTrueWhenTechnologyExists() {
        // Arrange
        List<Long> skillIds = List.of(1L);
        SkillEntity technologyEntity = new SkillEntity(1L, "Java", "description", 3);
        when(skillRepositoryMock.findAllById(skillIds)).thenReturn(Flux.just(technologyEntity));

        // Act
        Mono<Boolean> result = skillRepositoryImpl.exists(skillIds);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
        verify(skillRepositoryMock).findAllById(skillIds);
    }

    @Test
    @DisplayName("Exists returns false when skill does not exist")
    void existsReturnsFalseWhenSkillDoesNotExist() {
        // Arrange
        List<Long> skillIds = List.of(999L);
        when(skillRepositoryMock.findAllById(skillIds)).thenReturn(Flux.empty());

        // Act
        Mono<Boolean> result = skillRepositoryImpl.exists(skillIds);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
        verify(skillRepositoryMock).findAllById(skillIds);
    }

    @Test
    @DisplayName("Exists maps DataAccessException to GenericDataBaseException")
    void existsMapsException() {
        // Arrange
        List<Long> skillIds = List.of(1L);
        when(skillRepositoryMock.findAllById(skillIds)).thenReturn(Flux.error(new RuntimeException("Database error")));

        // Act
        Mono<Boolean> result = skillRepositoryImpl.exists(skillIds);

        // Assert
        StepVerifier.create(result)
                .expectError(GenericDatabaseException.class)
                .verify();
        verify(skillRepositoryMock).findAllById(skillIds);
    }

    @Test
    @DisplayName("get skills by ids goes well")
    void getSkillsByIdSuccess(){
        // Arrange
        Flux<SkillTechnologyEntity> fluxSkillTechnologyEntities = Flux.just(
                new SkillTechnologyEntity(1L,1L)
                , new SkillTechnologyEntity(2L,2L)
                , new SkillTechnologyEntity(3L,3L)
        );
        List<TechnologyExternalModel> technologyExternalModelList = List.of(
                new TechnologyExternalModel(1L,"")
                , new TechnologyExternalModel(2L,"")
                , new TechnologyExternalModel(3L,"")
        );
        SkillEntity skillEntity = new SkillEntity(1L,"name", "description", 3);
        SkillModel skillModel = new SkillModel(1L,"name", "description", technologyExternalModelList);
        Flux<SkillEntity> skillEntityFlux = Flux.just(skillEntity);

        when(skillRepositoryMock.findAllById(anyList())).thenReturn(skillEntityFlux);
        when(skillTechnologyRepositoryMock.findAllBySkillId(anyLong())).thenReturn(fluxSkillTechnologyEntities);

        // Act
        Flux<SkillModel> response = skillRepositoryImpl.getSkillsById(List.of(1L));

        // Assert
        StepVerifier.create(response)
                .expectNext(skillModel)
                .verifyComplete();
        verify(skillRepositoryMock).findAllById(anyList());
        verify(skillTechnologyRepositoryMock).findAllBySkillId(anyLong());
    }

    @Test
    @DisplayName("get skills by ids goes well even when one of the skill have not list of skill")
    void getSkillsByIdSuccessPartially(){
        // Arrange
        Flux<SkillTechnologyEntity> fluxSkillTechnologyEntities = Flux.just(
                new SkillTechnologyEntity(1L,1L)
                , new SkillTechnologyEntity(2L,2L)
                , new SkillTechnologyEntity(3L,3L)
        );
        List<TechnologyExternalModel> technologyExternalModelList = List.of(
                new TechnologyExternalModel(1L,"")
                , new TechnologyExternalModel(2L,"")
                , new TechnologyExternalModel(3L,"")
        );
        SkillEntity skillEntity1 = new SkillEntity(1L,"name", "description", 3);
        SkillEntity skillEntity2 = new SkillEntity(2L,"name2", "description2", 0);
        SkillModel skillModel = new SkillModel(1L,"name", "description", technologyExternalModelList);
        Flux<SkillEntity> skillEntityFlux = Flux.just(skillEntity1, skillEntity2);

        when(skillRepositoryMock.findAllById(anyList())).thenReturn(skillEntityFlux);
        when(skillTechnologyRepositoryMock.findAllBySkillId(anyLong())).thenReturn(fluxSkillTechnologyEntities).thenReturn(Flux.empty());

        // Act
        Flux<SkillModel> response = skillRepositoryImpl.getSkillsById(List.of(1L, 2L));

        // Assert
        StepVerifier.create(response)
                .expectNext(skillModel)
                .verifyComplete();
        verify(skillRepositoryMock).findAllById(anyList());
        verify(skillTechnologyRepositoryMock, times(2)).findAllBySkillId(anyLong());
    }
}
