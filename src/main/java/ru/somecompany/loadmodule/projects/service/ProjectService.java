package ru.somecompany.loadmodule.projects.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.projects.models.Project;
import ru.somecompany.loadmodule.projects.repository.ProjectRepository;

import java.util.List;

@Service("projectService")
public class ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }


    public List<Project> getAllProjectsByPage(int page, int size){

        return projectRepository.getAllProjectsByPage(PageRequest.of(page, size));

    }

    public Long getProjectsCount(){
        return projectRepository.count();
    }

    public Project getById(Long id){
        return projectRepository.findById(id).get();

    }

    public Long save(Project project){
        Project p = projectRepository.save(project);
        return p.getId();
    }

    public void delete(Project project){
        projectRepository.delete(project);
    }
}
