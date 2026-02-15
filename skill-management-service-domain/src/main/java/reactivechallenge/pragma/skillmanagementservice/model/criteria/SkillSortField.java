package reactivechallenge.pragma.skillmanagementservice.model.criteria;


public enum SkillSortField {
    NAME("name"),
    TOTAL_TECHNOLOGIES("totalTechnologies");

    private final String fieldName;

    SkillSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static SkillSortField fromString(String value) {
        if (value != null){
            for (SkillSortField field : SkillSortField.values()) {
                if (field.name().equalsIgnoreCase(value)) {
                    return field;
                }
            }
        }
        return NAME;
    }
}
