package reactivechallenge.pragma.skillmanagementservice.mapper;

import org.springframework.stereotype.Component;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;
import reactivechallenge.pragma.skillmanagementservice.exception.GenericDatabaseException;

@Component
public class DatabaseErrorMapper {

    public Throwable map(Throwable e) {
        if (e instanceof org.springframework.dao.DataIntegrityViolationException) {

            if (e.getMessage().contains("name_skill_unique")) {
                return new BusinessDomainException("Ya existe una capacidad con ese nombre registrado.");
            }

            return new BusinessDomainException("Error de integridad: verifica los datos enviados.");
        }

        return new GenericDatabaseException("Error inesperado en la base de datos");
    }
}
