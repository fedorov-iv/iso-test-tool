package ru.somecompany.loadmodule.terminals.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.terminals.forms.CopyTerminalForm;
import ru.somecompany.loadmodule.terminals.forms.TerminalForm;
import ru.somecompany.loadmodule.terminals.models.Terminal;
import ru.somecompany.loadmodule.terminals.repository.TerminalSearchRepository;
import ru.somecompany.loadmodule.terminals.service.TerminalsService;

import java.util.List;

@Controller
public class TerminalsController {

    private int pageSize = 10;

    @Autowired
    private TerminalsService terminalsService;

    @Autowired
    private TerminalSearchRepository terminalSearch;


    @RequestMapping(value = "/terminals", method = RequestMethod.GET)
    public ModelAndView terminals(@RequestParam(name = "page", required = false, defaultValue = "0") String page,
                                  @RequestParam(name="size", required=false, defaultValue="10") String size,
                                  @RequestParam(name="search", required=false, defaultValue="") String search) {

        ModelAndView modelAndView = new ModelAndView();

        int p = (!page.equals("0")) ? Integer.valueOf(page) : 1;
        int s = Integer.valueOf(size); //items on page

        Long terminalsCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<Terminal> terminals = terminalSearch.search(search, startAt, s);
            terminalsCount = (long) terminalSearch.getResultSize(search);
            modelAndView.addObject("terminals", terminals);

        }else{
            List<Terminal> terminals = terminalsService.getAllTerminalsByPage(p - 1, s);
            terminalsCount = terminalsService.getTerminalsCount();
            modelAndView.addObject("terminals", terminals);
        }

        int pagesCount = (int) Math.ceil((double) terminalsCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("terminals");
        return modelAndView;
    }

    @RequestMapping(value = "/terminals/add", method = RequestMethod.GET)
    public ModelAndView addTerminalForm() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("terminal", new Terminal());
        modelAndView.setViewName("edit_terminal");
        return modelAndView;

    }

    @RequestMapping(value = "/terminals/add", method = RequestMethod.POST)
    public String addTerminal(TerminalForm form) {

        Terminal terminal = new Terminal();
        terminal.setName(form.getName());
        terminal.setDescription(form.getDescription());
        terminal.setTerminalType(form.getTerminalType());
        terminal.setTerminalId(form.getTerminalId());
        terminal.setMerchantId(form.getMerchantId());
        terminal.setMcc(form.getMcc());
        terminal.setTpk(form.getTpk());
        terminalsService.save(terminal);
        return "redirect:/terminals";

    }

    @RequestMapping(value = "/terminals/edit", method = RequestMethod.GET)
    public ModelAndView editTerminal(@RequestParam(name="id") String id) {
        Long terminalId = Long.valueOf(id);
        Terminal terminal = terminalsService.getById(terminalId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("terminal", terminal);
        modelAndView.setViewName("edit_terminal");
        return modelAndView;

    }

    @RequestMapping(value = "/terminals/edit", method = RequestMethod.POST)
    public String editTerminal(TerminalForm form) {

        Terminal terminal = terminalsService.getById(form.getId());
        terminal.setName(form.getName());
        terminal.setDescription(form.getDescription());
        terminal.setTerminalType(form.getTerminalType());
        terminal.setTerminalId(form.getTerminalId());
        terminal.setMerchantId(form.getMerchantId());
        terminal.setMcc(form.getMcc());
        terminal.setTpk(form.getTpk());
        terminalsService.save(terminal);
        return "redirect:/terminals/edit/?id=" + terminal.getId();

    }

    @RequestMapping(value = "/terminals/delete", method = RequestMethod.GET)
    public String deleteTerminal(@RequestParam(name = "id") String id) {

        Long terminalId = Long.valueOf(id);
        Terminal terminal = terminalsService.getById(terminalId);
        terminalsService.delete(terminal);
        return "redirect:/terminals";

    }

    @RequestMapping(path="/terminals/copy", method = RequestMethod.POST)
    public String copyTerminal(CopyTerminalForm form){

        Terminal terminal = terminalsService.getById(form.getTerminalId());
        Terminal newTerminal = new Terminal();
        newTerminal.setName(form.getName());
        newTerminal.setDescription("");
        newTerminal.setTerminalType(terminal.getTerminalType());
        newTerminal.setTerminalId(terminal.getTerminalId());
        newTerminal.setMerchantId(terminal.getMerchantId());
        newTerminal.setMcc(terminal.getMcc());
        newTerminal.setTpk(terminal.getTpk());
        terminalsService.save(newTerminal);
        return "redirect:/terminals";
    }
}
