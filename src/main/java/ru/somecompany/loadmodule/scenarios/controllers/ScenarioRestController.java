package ru.somecompany.loadmodule.scenarios.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.repository.ParameterRepository;
import ru.somecompany.loadmodule.scenarios.models.Scenario;
import ru.somecompany.loadmodule.scenarios.repository.ScenarioRepository;
import ru.somecompany.loadmodule.scenarios.service.ScenarioService;
import ru.somecompany.loadmodule.steps.forms.StepFieldForm;
import ru.somecompany.loadmodule.steps.models.ScenarioStep;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.models.StepField;
import ru.somecompany.loadmodule.steps.repository.ScenarioStepRepository;
import ru.somecompany.loadmodule.steps.repository.StepFieldRepository;
import ru.somecompany.loadmodule.steps.repository.StepRepository;
import ru.somecompany.loadmodule.steps.service.StepFieldService;
import ru.somecompany.loadmodule.steps.service.StepsService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class ScenarioRestController {

    Logger logger = LoggerFactory.getLogger(ScenarioRestController.class);

    @Autowired
    private ParameterRepository parametersRepository;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private StepsService stepsService;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private ScenarioStepRepository scenarioStepRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    StepFieldRepository stepFieldRepository;

    @Autowired
    StepFieldService stepFieldService;


    @RequestMapping(value="scenarios/parameters/all", method=GET)
    public Iterable<Parameter> getParameters() {
        return parametersRepository.findAll();
    }

    @RequestMapping(value="scenarios/{scenarioId}/parameters", method=GET)
    public Iterable<Parameter> getAttachedParameters(@PathVariable(name="scenarioId") String scenarioId) {

        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        return scenario.getParameters();
    }

    @RequestMapping(value="scenarios/{scenarioId}/parameters", method=POST)
    public void attachParameters(@PathVariable(name="scenarioId") String scenarioId, @RequestBody List<String> paramIds) {

        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        paramIds.forEach(i -> scenario.addParameter(parametersRepository.findById(Long.valueOf(i)).get()));
        scenarioService.save(scenario);
    }

    @RequestMapping(value="scenarios/{scenarioId}/parameters/{parameterId}/remove", method=DELETE)
    public void removeParameter(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="parameterId") String paramId) {
        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        scenario.deleteParameter(parametersRepository.findById(Long.valueOf(paramId)).get());
        scenarioService.save(scenario);
    }


    @RequestMapping(value="scenarios/steps/all", method=GET)
    public Iterable<Step> getSteps() {
        return stepRepository.findAll();
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps", method=GET)
    public Iterable<ScenarioStep> getAttachedSteps(@PathVariable(name="scenarioId") String scenarioId) {
        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        return scenarioService.getScenarioSteps(scenario);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps", method=POST)
    public void attachSteps(@PathVariable(name="scenarioId") String scenarioId, @RequestBody List<String> stepIds) {

        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));

        stepIds.forEach(i -> {
            Step step = stepRepository.findById(Long.valueOf(i)).get();
            if(!scenarioService.scenarioStepExists(scenario, step)){
                ScenarioStep scenarioStep = new ScenarioStep();
                scenarioStep.setScenario(scenario);
                scenarioStep.setStep(step);
                scenarioStep.setSort(scenarioService.getMaxScenarioStepSort(scenario) + 10);
                scenarioStepRepository.save(scenarioStep);
            }
        });

    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/edit", method=GET)
    public Step showStep(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId) {
        return stepsService.getById(Long.valueOf(stepId));
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/fields", method=GET)
    public Iterable<StepField> getStepFields(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId) {
        Step step = stepsService.getById(Long.valueOf(stepId));
        return stepFieldRepository.findByStep(step);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/addfield", method=POST)
    public void addStepField(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId, @RequestBody StepFieldForm stepFieldForm) {
        Step step = stepsService.getById(Long.valueOf(stepId));
        StepField stepField = new StepField();
        stepField.setName(stepFieldForm.getName());
        stepField.setValue(stepFieldForm.getValue());
        stepField.setStep(step);
        stepFieldRepository.save(stepField);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/field", method=POST)
    public void editStepField(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId, @RequestBody StepFieldForm stepFieldForm) {
        StepField stepField = stepFieldService.getById(stepFieldForm.getId());
        stepField.setName(stepFieldForm.getName());
        stepField.setValue(stepFieldForm.getValue());
        stepFieldRepository.save(stepField);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/field/{fieldId}", method=DELETE)
    public void deleteStepField(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId, @PathVariable(name="fieldId") String fieldId) {
        StepField stepField = stepFieldService.getById(Long.valueOf(fieldId));
        stepFieldService.delete(stepField);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/edit", method=POST)
    public void editStep(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId, @RequestBody Step step) {
        Step current = stepsService.getById(Long.valueOf(stepId));
        current.setName(step.getName());
        current.setType(step.getType());
        current.setDescription(step.getDescription());
        stepRepository.save(current);

    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/remove", method=DELETE)
    public void removeStep(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId) {
        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        Step step = stepsService.getById(Long.valueOf(stepId));
        scenarioService.removeScenarioStep(scenario, step);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/moveup", method=POST)
    public void moveUpStep(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId) {
        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        Step step = stepsService.getById(Long.valueOf(stepId));
        scenarioService.moveScenarioStepUp(scenario, step);
    }

    @RequestMapping(value="scenarios/{scenarioId}/steps/{stepId}/movedown", method=POST)
    public void moveDownStep(@PathVariable(name="scenarioId") String scenarioId, @PathVariable(name="stepId") String stepId) {
        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));
        Step step = stepsService.getById(Long.valueOf(stepId));
        scenarioService.moveScenarioStepDown(scenario, step);
    }

    @RequestMapping(value="scenarios/{scenarioId}/attachstep", method=POST)
    public void createAndAttachStep(@PathVariable(name="scenarioId") String scenarioId, @RequestBody Step step) {
        Scenario scenario = scenarioService.getById(Long.valueOf(scenarioId));

        Step newStep = stepRepository.save(step);
        logger.info("Saved step with ID: {}", newStep.getId());

        ScenarioStep scenarioStep = new ScenarioStep();
        scenarioStep.setScenario(scenario);
        scenarioStep.setStep(newStep);
        scenarioStep.setSort(scenarioService.getMaxScenarioStepSort(scenario) + 10);
        scenarioStepRepository.save(scenarioStep);

    }



}
