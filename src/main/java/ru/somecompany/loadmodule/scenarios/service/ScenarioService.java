package ru.somecompany.loadmodule.scenarios.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.somecompany.loadmodule.scenarios.models.Scenario;
import ru.somecompany.loadmodule.scenarios.repository.ScenarioRepository;
import ru.somecompany.loadmodule.steps.models.ScenarioStep;
import ru.somecompany.loadmodule.steps.models.Step;
import ru.somecompany.loadmodule.steps.repository.ScenarioStepRepository;

import javax.persistence.EntityManager;
import java.util.List;

@Service("scenarioService")
public class ScenarioService {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioService.class);

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private ScenarioStepRepository scenarioStepRepository;

    @Autowired
    EntityManager entityManager;


    public List<Scenario> getAllScenariosByPage(int page, int size){

        return scenarioRepository.getAllScenariosByPage(PageRequest.of(page, size));

    }

    public Long getScenariosCount(){
        return scenarioRepository.count();
    }

    public Scenario getById(Long id){
        return scenarioRepository.findById(id).get();

    }

    public Long save(Scenario scenario){
        Scenario p = scenarioRepository.save(scenario);
        return p.getId();
    }

    public void delete(Scenario scenario){
        scenarioRepository.delete(scenario);
    }

    @SuppressWarnings("unchecked")
    public List<ScenarioStep> getScenarioSteps(Scenario scenario){
        String hql = "FROM ScenarioStep as ss WHERE ss.scenario = ?1 ORDER BY ss.sort";
        return (List<ScenarioStep>) entityManager.createQuery(hql).setParameter(1, scenario).getResultList();
    }

    public boolean scenarioStepExists(Scenario scenario, Step step){
        String hql = "FROM ScenarioStep as ss WHERE ss.scenario = ?1 AND ss.step = ?2";
        int count = entityManager.createQuery(hql).setParameter(1, scenario).setParameter(2, step).getResultList().size();
        return count > 0;

    }

    @Transactional
    public void removeScenarioStep(Scenario scenario, Step step){
        String hql = "delete FROM ScenarioStep as ss WHERE ss.scenario = ?1 AND ss.step = ?2";
        entityManager.createQuery(hql).setParameter(1, scenario).setParameter(2, step).executeUpdate();
    }

    public int getMaxScenarioStepSort(Scenario scenario){
        String hql = "select coalesce(MAX(ss.sort), 0) FROM ScenarioStep as ss WHERE ss.scenario = ?1";
        return (int) entityManager.createQuery(hql).setParameter(1, scenario).getSingleResult();
    }

    public void moveScenarioStepUp(Scenario scenario, Step step){
        List<ScenarioStep> scenarioStepList = getScenarioSteps(scenario);

        for(int i =0; i < scenarioStepList.size(); i++){
            ScenarioStep scenarioStep = scenarioStepList.get(i);
            if(scenarioStep.getStep().getId().equals(step.getId())){

                try{
                    ScenarioStep previousScenarioStep = scenarioStepList.get(i - 1);

                    int currSort = scenarioStep.getSort();
                    int prevSort = previousScenarioStep.getSort();

                    scenarioStep.setSort(prevSort);
                    previousScenarioStep.setSort(currSort);

                    scenarioStepRepository.save(previousScenarioStep);
                    scenarioStepRepository.save(scenarioStep);

                }catch (IndexOutOfBoundsException e){
                    logger.info(e.getMessage());
                }

            }
        }
    }



    public void moveScenarioStepDown(Scenario scenario, Step step){
        List<ScenarioStep> scenarioStepList = getScenarioSteps(scenario);

        for(int i =0; i < scenarioStepList.size(); i++){
            ScenarioStep scenarioStep = scenarioStepList.get(i);
            if(scenarioStep.getStep().getId().equals(step.getId())){

                try {
                    ScenarioStep nextScenarioStep = scenarioStepList.get(i + 1);

                    int currSort = scenarioStep.getSort();
                    int nextSort = nextScenarioStep.getSort();

                    scenarioStep.setSort(nextSort);
                    nextScenarioStep.setSort(currSort);

                    scenarioStepRepository.save(nextScenarioStep);
                    scenarioStepRepository.save(scenarioStep);

                }catch (IndexOutOfBoundsException e){

                    logger.info(e.getMessage());

                }

            }
        }
    }



}
