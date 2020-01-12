package ru.somecompany.loadmodule.steps.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.somecompany.loadmodule.steps.models.ScenarioStep;

@Repository
public interface ScenarioStepRepository extends CrudRepository<ScenarioStep, Long> {

}
