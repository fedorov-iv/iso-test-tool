package ru.somecompany.loadmodule.scenarios.controllers;

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
import ru.somecompany.loadmodule.parameters.models.Parameter;
import ru.somecompany.loadmodule.parameters.repository.ParameterRepository;
import ru.somecompany.loadmodule.scenarios.forms.CopyScenarioForm;
import ru.somecompany.loadmodule.scenarios.forms.ScenarioForm;
import ru.somecompany.loadmodule.scenarios.models.Scenario;
import ru.somecompany.loadmodule.scenarios.repository.ScenarioSearchRepository;
import ru.somecompany.loadmodule.scenarios.service.ScenarioService;
import ru.somecompany.loadmodule.terminals.models.Terminal;
import ru.somecompany.loadmodule.terminals.service.TerminalsService;

import java.util.List;

@Controller
public class ScenarioController {

    private int pageSize = 10;

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private CardsService cardsService;

    @Autowired
    private TerminalsService terminalsService;

    @Autowired
    private ScenarioSearchRepository scenarioSearch;

    @Autowired
    private ParameterRepository parametersRepository;

    @RequestMapping(value = "/scenarios", method = RequestMethod.GET)
    public ModelAndView scenarios(@RequestParam(name="page", required=false, defaultValue="0") String page,
                                 @RequestParam(name="size", required=false, defaultValue="10") String size,
                                 @RequestParam(name="search", required=false, defaultValue="") String search) {

        int p  = (!page.equals("0")) ? Integer.valueOf(page) : 1; // page
        int s = Integer.valueOf(size); //items on page

        ModelAndView modelAndView = new ModelAndView();

        Long scenariosCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Scenario> scenarios = scenarioSearch.search(search, startAt, s);
            modelAndView.addObject("scenarios", scenarios);
            scenariosCount = (long) scenarioSearch.getResultSize(search);

        }else{

            List<Scenario> scenarios = scenarioService.getAllScenariosByPage(p - 1, s);
            modelAndView.addObject("scenarios", scenarios);
            scenariosCount = scenarioService.getScenariosCount();
        }

        int pagesCount = (int) Math.ceil((double) scenariosCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("scenarios");
        return modelAndView;
    }

    @RequestMapping(value = "/scenarios/add", method = RequestMethod.GET)
    public ModelAndView addScenarioForm() {

        Iterable<Channel> channels = channelService.getAllChannels();
        Iterable<Card> cards = cardsService.getAllCards();
        Iterable<Terminal> terminals = terminalsService.getAllTerminals();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("channels", channels);
        modelAndView.addObject("cards", cards);
        modelAndView.addObject("terminals", terminals);
        modelAndView.addObject("scenario", new Scenario());
        modelAndView.setViewName("edit_scenario");
        return modelAndView;

    }

    @RequestMapping(value = "/scenarios/add", method = RequestMethod.POST)
    public String addScenario(ScenarioForm form) {

        Scenario scenario = new Scenario();
        scenario.setName(form.getName());
        scenario.setDescription(form.getDescription());
        scenario.setChannelId(form.getChannelId());
        scenario.setCardId(form.getCardId());
        scenario.setTerminalId(form.getTerminalId());
        scenarioService.save(scenario);

        return "redirect:/scenarios";

    }

    @RequestMapping(value = "/scenarios/edit", method = RequestMethod.GET)
    public ModelAndView editScenario(@RequestParam(name="id") String id) {

        Long scenarioId = Long.valueOf(id);
        Scenario scenario = scenarioService.getById(scenarioId);

        Iterable<Channel> channels = channelService.getAllChannels();
        Iterable<Card> cards = cardsService.getAllCards();
        Iterable<Terminal> terminals = terminalsService.getAllTerminals();

        Iterable<Parameter> parameters = parametersRepository.findAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("scenario", scenario);
        modelAndView.addObject("channels", channels);
        modelAndView.addObject("cards", cards);
        modelAndView.addObject("terminals", terminals);
        modelAndView.addObject("parameters", parameters);
        modelAndView.setViewName("edit_scenario");
        return modelAndView;

    }

    @RequestMapping(value = "/scenarios/edit", method = RequestMethod.POST)
    public String editScenario(ScenarioForm form) {

        Scenario scenario = scenarioService.getById(form.getId());
        scenario.setName(form.getName());
        scenario.setDescription(form.getDescription());
        scenario.setChannelId(form.getChannelId());
        scenario.setCardId(form.getCardId());
        scenario.setTerminalId(form.getTerminalId());
        scenarioService.save(scenario);

        return "redirect:/scenarios/edit/?id=" + scenario.getId();

    }


    @RequestMapping(value = "/scenarios/delete", method = RequestMethod.GET)
    public String deleteScenario(@RequestParam(name="id") String id) {
        Long scenarioId = Long.valueOf(id);
        Scenario scenario = scenarioService.getById(scenarioId);
        scenarioService.delete(scenario);
        return "redirect:/scenarios";

    }

    @RequestMapping(path="/scenarios/copy", method = RequestMethod.POST)
    public String copyScenario(CopyScenarioForm form){

        Scenario scenario = scenarioService.getById(form.getScenarioId());

        Scenario newScenario = new Scenario();
        newScenario.setName(form.getName());
        newScenario.setDescription(scenario.getDescription());
        newScenario.setChannelId(scenario.getChannelId());
        Long id = scenarioService.save(newScenario);
        Scenario createdScenario = scenarioService.getById(id);
        
        return "redirect:/scenarios";
    }

}
