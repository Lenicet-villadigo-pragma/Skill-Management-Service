package reactivechallenge.pragma.skillmanagementservice.mapper;

import org.springframework.stereotype.Component;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillTechnologyEntity;

@Component
public class SkillTechnologyEntityMapper {


    public SkillTechnologyEntity toEntity(SkillModel model, Long technologyId) {
        if (model == null || technologyId == null) {
            return null;
        }
        return new SkillTechnologyEntity(
                model.id(),
                technologyId
        );
    }

    public TechnologyExternalModel toTechnologyExternalModel(SkillTechnologyEntity skillTechnologyEntity){
        return new TechnologyExternalModel(skillTechnologyEntity.technologyId(), "");
    }
}
