package reactivechallenge.pragma.skillmanagementservice.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;

import java.util.List;

class SkillModelTest {

    @Test
    void createSkillModelSuccessful(){
        // Arrange
        SkillModel skillModel = null;
        List<TechnologyExternalModel> technologyExternalModelList = List.of(
                new TechnologyExternalModel(1L)
                ,new TechnologyExternalModel(2L)
                ,new TechnologyExternalModel(3L)
        );

        // Act
        try {
            skillModel = new SkillModel(null,"name", "description", technologyExternalModelList);
        }catch (Exception ex){
            System.out.println("error en el test"+ ex);
        }

        //Assert
        AssertionErrors.assertNotNull("Se espera que Skill model sea creado", skillModel);
    }

    @Test
    void createSkillModelFailsBecauseEmptyName(){
        // Arrange
        SkillModel skillModel = null;
        BusinessDomainException businessDomainException = null;
        List<TechnologyExternalModel> technologyExternalModelList = List.of(
                new TechnologyExternalModel(1L)
                ,new TechnologyExternalModel(2L)
                ,new TechnologyExternalModel(3L)
        );

        // Act
        try {
            skillModel = new SkillModel(null,"", "description", technologyExternalModelList);
        }catch (Exception ex){
            businessDomainException = (BusinessDomainException) ex;
        }

        //Assert
        AssertionErrors.assertNull("Se espera que Skill model no sea creado por nombre vacío o nulo", skillModel);
        AssertionErrors.assertNotNull("Se espera que BusinessDomainException sea lanzado", businessDomainException);
        Assertions.assertEquals("El nombre de la capacidad no puede estar vacío", businessDomainException.getMessage());
    }

    @Test
    void createSkillModelFailsBecauseNullTechs(){
        // Arrange
        SkillModel skillModel = null;
        BusinessDomainException businessDomainException = null;

        // Act
        try {
            skillModel = new SkillModel(null,"name", "description", null);
        }catch (Exception ex){
            businessDomainException = (BusinessDomainException) ex;
        }

        //Assert
        AssertionErrors.assertNull("Se espera que Skill model no sea creado por nombre vacío o nulo", skillModel);
        AssertionErrors.assertNotNull("Se espera que BusinessDomainException sea lanzado", businessDomainException);
        Assertions.assertEquals("La lista de tecnologías no puede ser nula, vacía, contener menos de 3 elementos" +
                        " o más de 20."
                , businessDomainException.getMessage());
    }

    @Test
    void createSkillModelReturnsTechIdsAsString(){
        // Arrange
        List<TechnologyExternalModel> technologyExternalModelList = List.of(
                new TechnologyExternalModel(1L)
                ,new TechnologyExternalModel(2L)
                ,new TechnologyExternalModel(3L)
        );
        SkillModel skillModel = new SkillModel(null,"name", "description", technologyExternalModelList);
        List<String> techIds = null;

        // Act
        techIds = skillModel.getTechnologyIdsAsString();

        //Assert
        AssertionErrors.assertNotNull("tech ids recibidos como string", techIds);
        Assertions.assertEquals("1", techIds.getFirst());
        Assertions.assertEquals("2", techIds.get(1));
        Assertions.assertEquals("3", techIds.getLast());
    }
}
