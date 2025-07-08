package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private UserServiceImpl userServiceImpl;
    private RoleServiceImpl roleServiceImpl;


    @Autowired
    public UserController(UserServiceImpl userService,
                          RoleServiceImpl roleService) {
        this.userServiceImpl = userService;
        this.roleServiceImpl = roleService;
    }

    @GetMapping
    public String showUser(Principal principal, Model model) {
        User u = userServiceImpl.findByUsername(principal.getName());

        model.addAttribute("users", List.of(u));
        model.addAttribute("roles", roleServiceImpl.getAllRoles());
        model.addAttribute("newUser", new User());
        model.addAttribute("principalEmail", u.getEmail());
        model.addAttribute("formattedRoles", RoleServiceImpl.humanRoles(u.getRoles()));

        return "user";
    }

    public static String humanRoles(Set<Role> roles) {
        return roles.stream()
                .map(r -> r.getName().replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
    }

}