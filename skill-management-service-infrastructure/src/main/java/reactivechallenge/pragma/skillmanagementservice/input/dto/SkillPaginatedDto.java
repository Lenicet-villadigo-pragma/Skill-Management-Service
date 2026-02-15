package reactivechallenge.pragma.skillmanagementservice.input.dto;

import java.util.List;

public record SkillPaginatedDto<T>(
        List<T> content,
        long totalElements,
        int pageNumber,
        int pageSize
) {}
