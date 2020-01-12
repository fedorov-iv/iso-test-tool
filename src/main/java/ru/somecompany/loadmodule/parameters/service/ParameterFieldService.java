package ru.somecompany.loadmodule.parameters.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.models.ParameterField;
import ru.somecompany.loadmodule.parameters.repository.ParameterFieldRepository;

import java.util.List;

@Service("parameterFieldService")
public class ParameterFieldService {
    private ParameterFieldRepository parameterFieldRepository;


    @Autowired
    public ParameterFieldService(ParameterFieldRepository parameterFieldRepository){
        this.parameterFieldRepository = parameterFieldRepository;
    }

    public List<ParameterField> getFieldsByParameter(Parameter parameter){
        return parameterFieldRepository.findByParameter(parameter);
    }

    public ParameterField getById(Long id){
        return parameterFieldRepository.findById(id).get();
    }

    public void save(ParameterField parameterField){
        parameterFieldRepository.save(parameterField);
    }

    public void delete(ParameterField parameterField){
        parameterFieldRepository.delete(parameterField);
    }
}
