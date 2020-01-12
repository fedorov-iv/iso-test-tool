package ru.somecompany.loadmodule.auth.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.somecompany.loadmodule.auth.forms.LogonForm;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LogonController {

    @GetMapping(value = "/")
    public String index() {
        return "redirect:/logon";
    }

    @GetMapping(value = "/logon")
    public String showFrom(Model model, boolean error, LogonForm logonForm) {

        if (error)
            model.addAttribute("error", "Invalid username or password");
        return "logon";
    }


    @GetMapping("/403")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return "403";

    }
}
