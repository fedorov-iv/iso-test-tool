package ru.somecompany.loadmodule.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.somecompany.loadmodule.auth.forms.AddUserForm;
import ru.somecompany.loadmodule.auth.forms.EditUserForm;
import ru.somecompany.loadmodule.auth.models.Role;
import ru.somecompany.loadmodule.auth.models.User;
import ru.somecompany.loadmodule.auth.repository.RoleRepository;
import ru.somecompany.loadmodule.auth.repository.UserRepository;
import ru.somecompany.loadmodule.auth.repository.UserSearchRepository;
import ru.somecompany.loadmodule.auth.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
public class AdminController {

    private int pageSize = 10;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserSearchRepository userSearch;


    /*@GetMapping
    public ModelAndView getAdminPage() {
        List<User> users = userService.getAllUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", users);
        modelAndView.addObject("roles", roleRepository.findAll());
        modelAndView.setViewName("admin");
        return modelAndView;
    }*/

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView users(@RequestParam(name = "page", required = false, defaultValue = "0") String page,
                              @RequestParam(name="size", required=false, defaultValue="10") String size,
                              @RequestParam(name="search", required=false, defaultValue="") String search) {

        ModelAndView modelAndView = new ModelAndView();
        int p = (!page.equals("0")) ? Integer.valueOf(page) : 1;
        int s = Integer.valueOf(size); //items on page

        Long usersCount = 0L;

        if(!search.isEmpty()){
            int startAt = s * (p  - 1);
            List<User> users = userSearch.search(search, startAt, s);
            modelAndView.addObject("users", users);
            usersCount = (long) userSearch.getResultSize(search);

        }else{
            List<User> users = userService.getAllUsersByPage(p - 1, s);
            usersCount = userService.getUsersCount();
            modelAndView.addObject("users", users);

        }

        int pagesCount = (int) Math.ceil((double) usersCount / (double) s);
        modelAndView.addObject("pages", pagesCount == 0 ? 1 : pagesCount);
        modelAndView.addObject("current_page", p);
        modelAndView.addObject("current_size", s);
        modelAndView.addObject("current_search", search);
        modelAndView.setViewName("users");
        return modelAndView;
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.GET)
    public ModelAndView userAddForm(AddUserForm addUserForm) {
        List<Role> roles = roleRepository.findAll();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("roles", roles);
        modelAndView.setViewName("add_user");
        return modelAndView;
    }

    @RequestMapping(value = "/users/add", method = RequestMethod.POST)
    public ModelAndView addUser(@Valid AddUserForm form, BindingResult bindingResult) {

        User u = userService.findByUsername(form.getUsername());
        List<Role> roles = roleRepository.findAll();

        if (u != null)
            bindingResult
                    .rejectValue("username", "error.username",
                            "There is already a user registered with the username provided");

        if (!form.getPassword().equals(form.getPasswordRepeat()))
            bindingResult
                    .rejectValue("password", "error.password",
                            "Passwords do not match");

        long[] roleIds = form.getRoles();


        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("roles", roles);
            modelAndView.setViewName("add_user");
        } else {


            User user = new User();
            user.setUsername(form.getUsername());
            user.setPassword(form.getPassword());
            user.setEnabled(form.isEnabled());

            List<Role> userRoles = new ArrayList<>();

            for (long i : roleIds) {
                userRoles.add(roleRepository.findById(i).get());

            }
            user.setRoles(new HashSet<>(userRoles));
            userService.add(user);
            return new ModelAndView("redirect:/users");
        }
        return modelAndView;

    }

    @RequestMapping(value = "/users/edit", method = RequestMethod.GET)
    public ModelAndView userEditForm(@RequestParam(name = "id") String id) {
        User user = userService.getById(Long.valueOf(id));
        List<Role> roles = roleRepository.findAll();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("roles", roles);
        modelAndView.setViewName("edit_user");
        return modelAndView;
    }

    @RequestMapping(value = "/users/edit", method = RequestMethod.POST)
    public ModelAndView editUser(@Valid EditUserForm form) {

        User user = userService.getById(form.getId());

        long[] roleIds = form.getRoles();

        user.setEnabled(form.isEnabled());
        List<Role> userRoles = new ArrayList<>();

        for (long i : roleIds) {
            userRoles.add(roleRepository.findById(i).get());

        }
        user.setRoles(new HashSet<>(userRoles));
        userService.save(user);

        return new ModelAndView("redirect:/users");

    }


    @RequestMapping(value = "/users/delete", method = RequestMethod.GET)
    public String deleteUser(@RequestParam(name = "id") String id) {
        Long userId = Long.valueOf(id);
        User u = userRepository.findById(userId).get();
        userService.delete(u);
        return "redirect:/users";

    }

    @RequestMapping(value = "/users/activate", method = RequestMethod.GET)
    public String activateUser(@RequestParam(name = "id") String id) {
        Long userId = Long.valueOf(id);
        User u = userRepository.findById(userId).get();

        if (u.isEnabled())
            u.setEnabled(false);
        else
            u.setEnabled(true);

        userRepository.save(u);
        return "redirect:/users";

    }


}
