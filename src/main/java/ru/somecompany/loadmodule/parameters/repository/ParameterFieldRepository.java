package ru.somecompany.loadmodule.parameters.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.models.ParameterField;

import java.util.List;

@Repository
public interface ParameterFieldRepository extends CrudRepository<ParameterField, Long> {
    List<ParameterField> findByParameter(Parameter parameter);
}
