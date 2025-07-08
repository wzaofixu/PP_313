package ru.kata.spring.boot_security.demo.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    List<User> findAllUsers();
    User saveUser(User user);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    User findById(Long id);
    void createUserWithRoles(User user, List<Long> role);
    void updateUserWithRoles(Long id, User user, List<Long> role);
    void deleteUser(Long id);
    void deleteUserWithChecks(Long id, String currentUsername);
    boolean isAdmin(User user);
    boolean hasRole(User user, String role);

}
