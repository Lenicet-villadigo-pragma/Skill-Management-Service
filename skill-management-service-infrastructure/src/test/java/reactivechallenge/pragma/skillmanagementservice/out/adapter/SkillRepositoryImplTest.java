package reactivechallenge.pragma.skillmanagementservice.out.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactivechallenge.pragma.skillmanagementservice.mapper.DatabaseErrorMapper;
import reactivechallenge.pragma.skillmanagementservice.mapper.SkillEntityMapper;
import reactivechallenge.pragma.skillmanagementservice.mapper.SkillTechnologyEntityMapper;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillEntity;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillTechnologyEntity;
import reactivechallenge.pragma.skillmanagementservice.out.repository.ISkillRepository;
import reactivechallenge.pragma.skillmanagementservice.out.repository.ISkillTechnologyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
         List<TechnologyExternalModel> technologies = List.of(new TechnologyExternalModel(1L),
                 new TechnologyExternalModel(21L), new TechnologyExternalModel(3L));
         SkillEntity skillEntityToBeSaved = new SkillEntity(null, "Java", "Description");
         SkillEntity skillEntitySaved = new SkillEntity(1L, "Java", "Description");
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

}
