package ru.somecompany.loadmodule.parameters.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.somecompany.loadmodule.parameters.forms.ParameterFieldForm;
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.models.ParameterField;
import ru.somecompany.loadmodule.parameters.service.ParameterFieldService;
import ru.somecompany.loadmodule.parameters.service.ParameterService;

@Controller
public class ParameterFieldController {

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private ParameterFieldService parameterFieldService;


    @RequestMapping(value = "/parameters/fields/add", method = RequestMethod.POST)
    public String addParameterField(ParameterFieldForm form) {

        Parameter parameter = parameterService.getById(form.getParameterId());

        ParameterField parameterField = new ParameterField();
        parameterField.setName(form.getName());
        parameterField.setValue(form.getValue());
        parameterField.setParameter(parameter);
        parameterFieldService.save(parameterField);

        return "redirect:/parameters/edit/?id=" + parameter.getId();

    }

    @RequestMapping(value = "/parameters/fields/edit", method = RequestMethod.POST)
    public String editParameterField(ParameterFieldForm form) {

        Parameter parameter = parameterService.getById(form.getParameterId());
        ParameterField parameterField = parameterFieldService.getById(form.getId());

        parameterField.setName(form.getName());
        parameterField.setValue(form.getValue());
        parameterField.setParameter(parameter);
        parameterFieldService.save(parameterField);

        return "redirect:/parameters/edit/?id=" + parameter.getId();

    }

    @RequestMapping(value = "/parameters/fields/delete", method = RequestMethod.GET)
    public String deleteParameterField(@RequestParam(name="id") String id) {
        Long parameterFieldId = Long.valueOf(id);

        ParameterField parameterField = parameterFieldService.getById(parameterFieldId);
        Long parameterId = parameterField.getParameter().getId();
        parameterFieldService.delete(parameterField);

        return "redirect:/parameters/edit/?id=" + parameterId;

    }
}
