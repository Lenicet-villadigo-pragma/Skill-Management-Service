package reactivechallenge.pragma.skillmanagementservice.input.dto;

import jakarta.validation.constraints.NotNull;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;

public record TechnologyExternalDto(
        @NotNull Long id
) {
    public TechnologyExternalModel toModel() {
        return new TechnologyExternalModel(this.id);
    }

    public static TechnologyExternalDto fromModel(TechnologyExternalModel model) {
        return new TechnologyExternalDto(model.id());
    }
}
