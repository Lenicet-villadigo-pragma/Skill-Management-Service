package reactivechallenge.pragma.skillmanagementservice.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;

import java.util.List;

public record SkillCreateDto (
        @NotNull @NotBlank @Size(max = 50, message = "El nombre debe tener máximo 50 caracteres", min = 1) String name,
        @Size(max = 90, message = "El nombre debe tener máximo 50 caracteres", min = 1) String description,
        @NotNull List<TechnologyExternalDto> technologyIds
) {
    public SkillModel toModel() {
        return new SkillModel(null, this.name, this.description, this.technologyIds.stream().map(TechnologyExternalDto::toModel).toList());
    }

    public static  SkillCreateDto fromModel(SkillModel model) {
        return new SkillCreateDto(model.name(), model.description(), model.technologies().stream().map(TechnologyExternalDto::fromModel).toList());
    }
}
