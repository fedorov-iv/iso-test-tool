package ru.somecompany.loadmodule.steps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.somecompany.loadmodule.steps.forms.StepFieldForm;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.models.StepField;
import ru.somecompany.loadmodule.steps.service.StepFieldService;
import ru.somecompany.loadmodule.steps.service.StepsService;

@Controller
public class StepFieldController {

    @Autowired
    private StepsService stepService;

    @Autowired
    private StepFieldService stepFieldService;


    @RequestMapping(value = "/steps/fields/add", method = RequestMethod.POST)
    public String addStepField(StepFieldForm form) {

        Step step = stepService.getById(form.getStepId());

        StepField stepField = new StepField();
        stepField.setName(form.getName());
        stepField.setValue(form.getValue());
        stepField.setStep(step);
        stepFieldService.save(stepField);

        return "redirect:/steps/edit/?id=" + step.getId();

    }

    @RequestMapping(value = "/steps/fields/edit", method = RequestMethod.POST)
    public String editStepField(StepFieldForm form) {

        Step step = stepService.getById(form.getStepId());
        StepField stepField = stepFieldService.getById(form.getId());

        stepField.setName(form.getName());
        stepField.setValue(form.getValue());
        stepField.setStep(step);
        stepFieldService.save(stepField);

        return "redirect:/steps/edit/?id=" + step.getId();

    }

    @RequestMapping(value = "/steps/fields/delete", method = RequestMethod.GET)
    public String deleteStepField(@RequestParam(name="id") String id) {
        Long stepFieldId = Long.valueOf(id);

        StepField stepField = stepFieldService.getById(stepFieldId);
        Long stepId = stepField.getStep().getId();
        stepFieldService.delete(stepField);

        return "redirect:/steps/edit/?id=" + stepId;

    }
}
