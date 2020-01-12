package ru.somecompany.loadmodule.parameters.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.repository.ParameterRepository;

import java.util.List;

@Service("parameterService")
public class ParameterService {

    private ParameterRepository parameterRepository;

    @Autowired
    public ParameterService(ParameterRepository parameterRepository){
        this.parameterRepository = parameterRepository;
    }


    public List<Parameter> getAllParametersByPage(int page, int size){

        return parameterRepository.getAllParametersByPage(PageRequest.of(page, size));

    }

    public Long getParametersCount(){
        return parameterRepository.count();
    }

    public Parameter getById(Long id){
        return parameterRepository.findById(id).get();

    }

    public Long save(Parameter parameter){
        Parameter p = parameterRepository.save(parameter);
        return p.getId();
    }

    public void delete(Parameter parameter){
        parameterRepository.delete(parameter);
    }
}
