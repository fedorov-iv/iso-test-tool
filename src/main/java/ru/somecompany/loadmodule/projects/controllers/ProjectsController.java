package ru.somecompany.loadmodule.projects.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.cards.models.Card;
import ru.somecompany.loadmodule.cards.service.CardsService;
import ru.somecompany.loadmodule.channels.models.Channel;
import ru.somecompany.loadmodule.channels.service.ChannelService;
import ru.somecompany.loadmodule.projects.forms.CopyProjectForm;
import ru.somecompany.loadmodule.projects.forms.ProjectForm;
import ru.somecompany.loadmodule.projects.models.Project;
import ru.somecompany.loadmodule.projects.models.ProjectField;
import ru.somecompany.loadmodule.projects.repository.ProjectSearchRepository;
import ru.somecompany.loadmodule.projects.service.ProjectFieldService;
import ru.somecompany.loadmodule.projects.service.ProjectService;
import ru.somecompany.loadmodule.terminals.models.Terminal;
import ru.somecompany.loadmodule.terminals.service.TerminalsService;

import java.util.List;

@Controller
public class ProjectsController {

    private int pageSize = 10;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFieldService projectFieldService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private CardsService cardsService;

    @Autowired
    private TerminalsService terminalsService;

    @Autowired
    private ProjectSearchRepository projectSearch;

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public ModelAndView projects(@RequestParam(name="page", required=false, defaultValue="0") String page,
                                 @RequestParam(name="size", required=false, defaultValue="10") String size,
                                 @RequestParam(name="search", required=false, defaultValue="") String search) {

        int p  = (!page.equals("0")) ? Integer.valueOf(page) : 1; // page
        int s = Integer.valueOf(size); //items on page

        ModelAndView modelAndView = new ModelAndView();

        Long projectsCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Project> projects = projectSearch.search(search, startAt, s);
            modelAndView.addObject("projects", projects);
            projectsCount = (long) projectSearch.getResultSize(search);

        }else{

            List<Project> projects = projectService.getAllProjectsByPage(p - 1, s);
            modelAndView.addObject("projects", projects);
            projectsCount = projectService.getProjectsCount();
        }

        int pagesCount = (int) Math.ceil((double) projectsCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("projects");
        return modelAndView;
    }

    @RequestMapping(value = "/projects/add", method = RequestMethod.GET)
    public ModelAndView addProjectForm() {

        Iterable<Channel> channels = channelService.getAllChannels();
        Iterable<Card> cards = cardsService.getAllCards();
        Iterable<Terminal> terminals = terminalsService.getAllTerminals();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("channels", channels);
        modelAndView.addObject("cards", cards);
        modelAndView.addObject("terminals", terminals);
        modelAndView.addObject("project", new Project());
        modelAndView.setViewName("edit_project");
        return modelAndView;

    }

    @RequestMapping(value = "/projects/add", method = RequestMethod.POST)
    public String addProject(ProjectForm form) {

        Project project = new Project();
        project.setName(form.getName());
        project.setDescription(form.getDescription());
        project.setChannelId(form.getChannelId());
        project.setCardId(form.getCardId());
        project.setTerminalId(form.getTerminalId());
        projectService.save(project);

        return "redirect:/projects";

    }

    @RequestMapping(value = "/projects/edit", method = RequestMethod.GET)
    public ModelAndView editProject(@RequestParam(name="id") String id) {

        Long projectId = Long.valueOf(id);
        Project project = projectService.getById(projectId);

        List<ProjectField> projectFields = projectFieldService.getFieldsByProject(project);
        //List<ProjectField> projectFields = new ArrayList<>();

        Iterable<Channel> channels = channelService.getAllChannels();
        Iterable<Card> cards = cardsService.getAllCards();
        Iterable<Terminal> terminals = terminalsService.getAllTerminals();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("project", project);
        modelAndView.addObject("channels", channels);
        modelAndView.addObject("cards", cards);
        modelAndView.addObject("terminals", terminals);
        modelAndView.addObject("project_fields", projectFields);
        modelAndView.setViewName("edit_project");
        return modelAndView;

    }

    @RequestMapping(value = "/projects/edit", method = RequestMethod.POST)
    public String editProject(ProjectForm form) {

        Project project = projectService.getById(form.getId());
        project.setName(form.getName());
        project.setDescription(form.getDescription());
        project.setChannelId(form.getChannelId());
        project.setCardId(form.getCardId());
        project.setTerminalId(form.getTerminalId());
        projectService.save(project);

        return "redirect:/projects/edit/?id=" + project.getId();

    }


    @RequestMapping(value = "/projects/delete", method = RequestMethod.GET)
    public String deleteProject(@RequestParam(name="id") String id) {
        Long projectId = Long.valueOf(id);
        Project project = projectService.getById(projectId);
        projectService.delete(project);
        return "redirect:/projects";

    }

    @RequestMapping(path="/projects/copy", method = RequestMethod.POST)
    public String copyProject(CopyProjectForm form){

        Project project = projectService.getById(form.getProjectId());
        List<ProjectField> projectFields = projectFieldService.getFieldsByProject(project);

        Project newProject = new Project();
        newProject.setName(form.getName());
        newProject.setDescription(project.getDescription());
        newProject.setChannelId(project.getChannelId());
        Long id = projectService.save(newProject);
        Project createdProject = projectService.getById(id);

        projectFields.forEach(f->{
            ProjectField pf = new ProjectField();
            pf.setProject(createdProject);
            pf.setName(f.getName());
            pf.setValue(f.getValue());
            pf.setDescription(f.getDescription());
            projectFieldService.save(pf);

        });

        return "redirect:/projects";
    }



}
