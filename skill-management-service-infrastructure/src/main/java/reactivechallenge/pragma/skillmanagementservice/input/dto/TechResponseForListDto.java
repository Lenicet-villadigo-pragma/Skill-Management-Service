package reactivechallenge.pragma.skillmanagementservice.input.dto;

import jakarta.validation.constraints.NotNull;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;

public record TechResponseForListDto(
        @NotNull Long id,
        @NotNull String name
) {
    public static TechResponseForListDto fromModel(TechnologyExternalModel model) {
        return new TechResponseForListDto(model.id(),model.name());
    }
}
