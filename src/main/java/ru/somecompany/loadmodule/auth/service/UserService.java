package ru.somecompany.loadmodule.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.somecompany.loadmodule.auth.models.User;
import ru.somecompany.loadmodule.auth.repository.RoleRepository;
import ru.somecompany.loadmodule.auth.repository.UserRepository;

import java.util.List;


@Service("userService")
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void add(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(User user){
        userRepository.delete(user);
    }

    public User getById(Long id){
        return userRepository.findById(id).get();
    }

    public Long getUsersCount(){
        return userRepository.count();
    }

    public List<User> getAllUsersByPage(int page, int size){
        return userRepository.getAllUsersByPage(PageRequest.of(page, size));
    }



}
