package ru.somecompany.loadmodule.steps.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.repository.StepRepository;


import java.util.List;

@Service("stepsService")
public class StepsService {
    private StepRepository stepsRepository;

    @Autowired
    public StepsService(StepRepository stepsRepository){
        this.stepsRepository = stepsRepository;
    }

    public Iterable<Step> getAllSteps(){
        return stepsRepository.findAll();

    }

    public List<Step> getAllStepsByPage(int page, int size){
        return stepsRepository.getAllStepsByPage(PageRequest.of(page, size));
    }

    public Step getById(Long id){
        return stepsRepository.findById(id).get();

    }

    public Long getStepsCount(){
        return stepsRepository.count();
    }

    public void save(Step step){
        stepsRepository.save(step);
    }

    public void delete(Step step){
        stepsRepository.delete(step);
    }
}
