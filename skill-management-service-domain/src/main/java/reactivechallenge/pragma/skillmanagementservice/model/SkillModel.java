package reactivechallenge.pragma.skillmanagementservice.model;

import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;

import java.util.List;

public record SkillModel(Long id, String name, String description, List<TechnologyExternalModel> technologies) {

    public SkillModel {
        validateName(name);
        validateTechnologies(technologies);
        technologies = List.copyOf(technologies);
    }

    private  void validateName(String name) {
        String nameValidated = name == null ? "" : name.trim();
        if (nameValidated.isBlank()) {
            throw new BusinessDomainException("El nombre de la capacidad no puede estar vacío");
        }
    }

    private void validateTechnologies(List<TechnologyExternalModel> technologies) {
        if (technologies == null || technologies.isEmpty() || technologies.size() <= 2 || technologies.size() > 20) {
            throw new BusinessDomainException("La lista de tecnologías no puede ser nula, vacía, " +
                    "contener menos de 3 elementos o más de 20.");
        }

        // Validar que no haya tecnologías duplicadas
        long uniqueTechnologiesCount = technologies.stream()
                .map(TechnologyExternalModel::id)
                .distinct()
                .count();
        if (uniqueTechnologiesCount < technologies.size()) {
            throw new BusinessDomainException("La lista de tecnologías no puede contener elementos duplicados");
        }
    }

    public List<String> getTechnologyIdsAsString() {
        return technologies.stream()
                .map(TechnologyExternalModel::id)
                .map(String::valueOf)
                .toList();
    }
}
