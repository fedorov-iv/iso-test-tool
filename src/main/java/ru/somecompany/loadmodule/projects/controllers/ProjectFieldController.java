package ru.somecompany.loadmodule.projects.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.somecompany.loadmodule.projects.forms.ProjectFieldForm;
import ru.somecompany.loadmodule.projects.models.Project;
import ru.somecompany.loadmodule.projects.models.ProjectField;
import ru.somecompany.loadmodule.projects.service.ProjectFieldService;
import ru.somecompany.loadmodule.projects.service.ProjectService;
import ru.somecompany.loadmodule.util.IsoFieldVerboseMapper;

@Controller
public class ProjectFieldController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFieldService projectFieldService;


    @RequestMapping(value = "/projects/fields/add", method = RequestMethod.POST)
    public String addProjectField(ProjectFieldForm form) {

        Project project = projectService.getById(form.getProjectId());

        ProjectField projectField = new ProjectField();
        projectField.setName(form.getName());
        projectField.setDescription(IsoFieldVerboseMapper.getVerboseName(form.getName()));
        projectField.setValue(form.getValue());
        projectField.setProject(project);
        projectFieldService.save(projectField);

        return "redirect:/projects/edit/?id=" + project.getId();

    }

    @RequestMapping(value = "/projects/fields/edit", method = RequestMethod.POST)
    public String editProjectField(ProjectFieldForm form) {

        Project project = projectService.getById(form.getProjectId());
        ProjectField projectField = projectFieldService.getById(form.getId());

        projectField.setName(form.getName());
        projectField.setDescription(IsoFieldVerboseMapper.getVerboseName(form.getName()));
        projectField.setValue(form.getValue());
        projectField.setProject(project);
        projectFieldService.save(projectField);

        return "redirect:/projects/edit/?id=" + project.getId();

    }

    @RequestMapping(value = "/projects/fields/delete", method = RequestMethod.GET)
    public String deleteProjectField(@RequestParam(name="id") String id) {
        Long projectFieldId = Long.valueOf(id);

        ProjectField projectField = projectFieldService.getById(projectFieldId);
        Long projectId = projectField.getProject().getId();
        projectFieldService.delete(projectField);

        return "redirect:/projects/edit/?id=" + projectId;

    }
}
