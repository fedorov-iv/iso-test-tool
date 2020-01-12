package ru.somecompany.loadmodule.steps.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.models.StepField;
import java.util.List;


@Repository
public interface StepFieldRepository extends CrudRepository<StepField, Long> {

    List<StepField> findByStep(Step step);
}
