package reactivechallenge.pragma.skillmanagementservice.model.criteria;

public enum SkillSortOrder {
    DESC("desc"),
    ASC("asc");

    private final String sortOrder;

    SkillSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public static SkillSortOrder fromString(String value) {
        if (value != null){
            for (SkillSortOrder order : SkillSortOrder.values()) {
                if (order.name().equalsIgnoreCase(value)) {
                    return order;
                }
            }
        }
        return ASC;
    }
}
