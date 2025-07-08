package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserServiceImpl userService, RoleServiceImpl roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showAllUsers(Model model, Principal principal) {
        User current = userService.findByUsername(principal.getName());
        model.addAttribute("newUser", new User());
        model.addAttribute("allUsers", userService.findAllUsers());
        model.addAttribute("users", List.of(current));
        model.addAttribute("principalEmail", current.getEmail());
        model.addAttribute("formattedRoles", RoleServiceImpl.humanRoles(current.getRoles()));
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin";
    }

    @PostMapping("/save-user")
    public String saveUser(@ModelAttribute("user") User newUser,
                           @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        userService.createUserWithRoles(newUser, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute User updated,
                             @RequestParam("roles") List<Long> roleIds) {
        userService.updateUserWithRoles(updated.getId(), updated, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("delete-user")
    public String deleteUser(@RequestParam("id") Long id, Principal principal) {
        userService.deleteUserWithChecks(id, principal.getName());
        return principal.getName().equals(userService.findById(id).getUsername())
                ? "redirect:/login" : "redirect:/admin";
    }

}