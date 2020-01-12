package ru.somecompany.loadmodule.parameters.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.parameters.forms.ParameterForm;
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.models.ParameterField;
import ru.somecompany.loadmodule.parameters.repository.ParameterSearchRepository;
import ru.somecompany.loadmodule.parameters.service.ParameterFieldService;
import ru.somecompany.loadmodule.parameters.service.ParameterService;

import java.util.List;


@Controller
public class ParametersController {
    private int pageSize = 10;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private ParameterFieldService parameterFieldService;


    @Autowired
    private ParameterSearchRepository parameterSearch;

    @RequestMapping(value = "/parameters", method = RequestMethod.GET)
    public ModelAndView parameters(@RequestParam(name="page", required=false, defaultValue="0") String page,
                                 @RequestParam(name="size", required=false, defaultValue="10") String size,
                                 @RequestParam(name="search", required=false, defaultValue="") String search) {

        int p  = (!page.equals("0")) ? Integer.valueOf(page) : 1; // page
        int s = Integer.valueOf(size); //items on page

        ModelAndView modelAndView = new ModelAndView();

        Long parametersCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Parameter> parameters = parameterSearch.search(search, startAt, s);
            modelAndView.addObject("parameters", parameters);
            parametersCount = (long) parameterSearch.getResultSize(search);

        }else{

            List<Parameter> parameters = parameterService.getAllParametersByPage(p - 1, s);
            modelAndView.addObject("parameters", parameters);
            parametersCount = parameterService.getParametersCount();
        }

        int pagesCount = (int) Math.ceil((double) parametersCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("parameters");
        return modelAndView;
    }

    @RequestMapping(value = "/parameters/add", method = RequestMethod.GET)
    public ModelAndView addParameterForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("parameter", new Parameter());
        modelAndView.setViewName("edit_parameter");
        return modelAndView;

    }

    @RequestMapping(value = "/parameters/add", method = RequestMethod.POST)
    public String addParameter(ParameterForm form) {

        Parameter parameter = new Parameter();
        parameter.setName(form.getName());
        parameter.setCode(form.getCode());
        parameter.setDescription(form.getDescription());
        parameterService.save(parameter);

        return "redirect:/parameters";

    }

    @RequestMapping(value = "/parameters/edit", method = RequestMethod.GET)
    public ModelAndView editParameter(@RequestParam(name="id") String id) {

        Long parameterId = Long.valueOf(id);
        Parameter parameter = parameterService.getById(parameterId);

        List<ParameterField> parameterFields = parameterFieldService.getFieldsByParameter(parameter);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("parameter", parameter);
        modelAndView.addObject("parameter_fields", parameterFields);
        modelAndView.setViewName("edit_parameter");
        return modelAndView;

    }

    @RequestMapping(value = "/parameters/edit", method = RequestMethod.POST)
    public String editParameter(ParameterForm form) {

        Parameter parameter = parameterService.getById(form.getId());
        parameter.setName(form.getName());
        parameter.setCode(form.getCode());
        parameter.setDescription(form.getDescription());
        parameterService.save(parameter);
        return "redirect:/parameters/edit/?id=" + parameter.getId();

    }


    @RequestMapping(value = "/parameters/delete", method = RequestMethod.GET)
    public String deleteParameter(@RequestParam(name="id") String id) {
        Long parameterId = Long.valueOf(id);
        Parameter parameter = parameterService.getById(parameterId);
        parameterService.delete(parameter);
        return "redirect:/parameters";

    }

}
