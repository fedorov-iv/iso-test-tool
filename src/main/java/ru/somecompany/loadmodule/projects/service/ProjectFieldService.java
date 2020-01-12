package ru.somecompany.loadmodule.projects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.projects.models.Project;
import ru.somecompany.loadmodule.projects.models.ProjectField;
import ru.somecompany.loadmodule.projects.repository.ProjectFieldRepository;

import java.util.List;

@Service("projectFieldService")
public class ProjectFieldService {

    private ProjectFieldRepository projectFieldRepository;


    @Autowired
    public ProjectFieldService(ProjectFieldRepository projectFieldRepository){
        this.projectFieldRepository = projectFieldRepository;
    }

    public List<ProjectField> getFieldsByProject(Project project){
        return projectFieldRepository.findByProject(project);
    }

    public ProjectField getById(Long id){
        return projectFieldRepository.findById(id).get();
    }

    public void save(ProjectField projectField){
        projectFieldRepository.save(projectField);
    }

    public void delete(ProjectField projectField){
        projectFieldRepository.delete(projectField);
    }


}
