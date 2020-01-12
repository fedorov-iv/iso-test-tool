package ru.somecompany.loadmodule.steps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.steps.forms.StepForm;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.models.StepField;
import ru.somecompany.loadmodule.steps.repository.StepSearchRepository;
import ru.somecompany.loadmodule.steps.service.StepFieldService;
import ru.somecompany.loadmodule.steps.service.StepsService;

import java.util.List;

@Controller
public class StepsController {
    
    @Autowired
    StepsService stepService;
    
    @Autowired
    StepSearchRepository stepSearch;

    @Autowired
    StepFieldService stepFieldService;

    @RequestMapping(value = "/steps", method = RequestMethod.GET)
    public ModelAndView steps(@RequestParam(name="page", required=false, defaultValue="0") String page,
                                 @RequestParam(name="size", required=false, defaultValue="10") String size,
                                 @RequestParam(name="search", required=false, defaultValue="") String search) {

        int p  = (!page.equals("0")) ? Integer.valueOf(page) : 1; // page
        int s = Integer.valueOf(size); //items on page

        ModelAndView modelAndView = new ModelAndView();

        Long stepsCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Step> steps = stepSearch.search(search, startAt, s);
            modelAndView.addObject("steps", steps);
            stepsCount = (long) stepSearch.getResultSize(search);

        }else{

            List<Step> steps = stepService.getAllStepsByPage(p - 1, s);
            modelAndView.addObject("steps", steps);
            stepsCount = stepService.getStepsCount();
        }

        int pagesCount = (int) Math.ceil((double) stepsCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("steps");
        return modelAndView;
    }

    @RequestMapping(value = "/steps/add", method = RequestMethod.GET)
    public ModelAndView addStepForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("step", new Step());
        modelAndView.setViewName("edit_step");
        return modelAndView;

    }

    @RequestMapping(value = "/steps/add", method = RequestMethod.POST)
    public String addStep(StepForm form) {

        Step step = new Step();
        step.setName(form.getName());
        step.setDescription(form.getDescription());
        step.setType(form.getType());
        stepService.save(step);

        return "redirect:/steps";

    }

    @RequestMapping(value = "/steps/edit", method = RequestMethod.GET)
    public ModelAndView editStep(@RequestParam(name="id") String id) {

        Long stepId = Long.valueOf(id);
        Step step = stepService.getById(stepId);

        List<StepField> stepFields = stepFieldService.getFieldsByStep(step);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("step", step);

        modelAndView.addObject("step_fields", stepFields);
        modelAndView.setViewName("edit_step");
        return modelAndView;

    }

    @RequestMapping(value = "/steps/edit", method = RequestMethod.POST)
    public String editStep(StepForm form) {

        Step step = stepService.getById(form.getId());
        step.setName(form.getName());
        step.setDescription(form.getDescription());
        step.setType(form.getType());
        stepService.save(step);

        return "redirect:/steps/edit/?id=" + step.getId();

    }


    @RequestMapping(value = "/steps/delete", method = RequestMethod.GET)
    public String deleteStep(@RequestParam(name="id") String id) {
        Long stepId = Long.valueOf(id);
        Step step = stepService.getById(stepId);
        stepService.delete(step);
        return "redirect:/steps";

    }

   /* @RequestMapping(path="/steps/copy", method = RequestMethod.POST)
    public String copyStep(CopyStepForm form){

        Step step = stepService.getById(form.getStepId());
        List<StepField> stepFields = stepFieldService.getFieldsByStep(step);

        Step newStep = new Step();
        newStep.setName(form.getName());
        newStep.setDescription(step.getDescription());
        newStep.setChannelId(step.getChannelId());
        Long id = stepService.save(newStep);
        Step createdStep = stepService.getById(id);

        stepFields.forEach(f->{
            StepField pf = new StepField();
            pf.setStep(createdStep);
            pf.setName(f.getName());
            pf.setValue(f.getValue());
            pf.setDescription(f.getDescription());
            stepFieldService.save(pf);

        });

        return "redirect:/steps";
    }*/
}
