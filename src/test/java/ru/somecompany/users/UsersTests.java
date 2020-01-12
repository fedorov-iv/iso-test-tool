package ru.somecompany.users;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.somecompany.loadmodule.Application;
import ru.somecompany.loadmodule.auth.models.Role;
import ru.somecompany.loadmodule.auth.repository.RoleRepository;
import ru.somecompany.loadmodule.auth.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UsersTests {


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @Test
    public void getRolesTest(){
        Role roleAdmin = roleRepository.findByRolename("ROLE_ADMIN");
        Assert.assertTrue(roleAdmin.getId() == 2);
        //System.out.println(roleAdmin.getId());
    }

    /*@Test
    public void addUserTest(){
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.setEnabled(true);

        List<Role> userRoles = new ArrayList<>();
        //userRoles.add();
        userRoles.add(roleRepository.findById(2L).get());
        user.setRoles(new HashSet<>(userRoles));

        userService.add(user);

    }*/
}
