package reactivechallenge.pragma.skillmanagementservice.model.criteria;

import java.util.List;

public record SkillPaginationResult<T>(
        List<T> items,
        long total
) {}