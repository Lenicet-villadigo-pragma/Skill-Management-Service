package reactivechallenge.pragma.skillmanagementservice.mapper;

import org.springframework.stereotype.Component;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.out.entity.SkillEntity;

import java.util.Collections;
import java.util.List;

@Component
public class SkillEntityMapper {
    public SkillModel toModel(SkillEntity entity, List<TechnologyExternalModel> technologyExternalModelList) {
        if (entity == null) {
            return null;
        }
        return new SkillModel(
                entity.id(),
                entity.name(),
                entity.description(),
                technologyExternalModelList
        );
    }

    public SkillEntity toEntity(SkillModel model) {
        if (model == null) {
            return null;
        }
        return new SkillEntity(
                model.id(),
                model.name(),
                model.description(),
                model.technologies().size()
        );
    }
}
