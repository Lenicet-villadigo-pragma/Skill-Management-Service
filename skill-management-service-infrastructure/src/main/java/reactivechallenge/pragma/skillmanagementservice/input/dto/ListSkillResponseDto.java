package reactivechallenge.pragma.skillmanagementservice.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;

import java.util.List;

public record ListSkillResponseDto(
        @NotNull Long id,
        @NotNull @NotBlank String name,
        @NotNull List<TechResponseForListDto> technologyIds
){
    public static ListSkillResponseDto fromModel(SkillModel skillModel, List<TechResponseForListDto> technologiesModel) {
        return new ListSkillResponseDto(skillModel.id(), skillModel.name(),technologiesModel);
    }

}
