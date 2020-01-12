package ru.somecompany.loadmodule.steps.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.models.StepField;
import ru.somecompany.loadmodule.steps.repository.StepFieldRepository;

import java.util.List;

@Service("stepFieldService")
public class StepFieldService {

    private StepFieldRepository stepFieldRepository;


    @Autowired
    public StepFieldService(StepFieldRepository stepFieldRepository){
        this.stepFieldRepository = stepFieldRepository;
    }

    public List<StepField> getFieldsByStep(Step step){
        return stepFieldRepository.findByStep(step);
    }

    public StepField getById(Long id){
        return stepFieldRepository.findById(id).get();
    }

    public void save(StepField stepField){
        stepFieldRepository.save(stepField);
    }

    public void delete(StepField stepField){
        stepFieldRepository.delete(stepField);
    }
}
