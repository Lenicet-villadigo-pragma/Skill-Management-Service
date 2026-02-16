package reactivechallenge.pragma.skillmanagementservice.input.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ListSkillResponseDto(
        @NotNull Long id,
        @NotNull @NotBlank String name,
        @NotNull List<TechResponseForListDto> technologyIds
){}
