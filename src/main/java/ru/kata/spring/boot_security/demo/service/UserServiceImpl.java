package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден ");
        }
        return user;
    }

    @Override
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    public void createUserWithRoles(User user, List<Long> roleIds) {
        Set<Role> roles;
        if(roleIds != null && !roleIds.isEmpty()) {
            roles = Set.of(roleService.findByName("ROLE_USER"));
        } else {
            roles = new HashSet<>(roleService.findRolesByIds(roleIds));
        }
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public void updateUserWithRoles(Long id, User updatedUser, List<Long> roleIds) {

        User existing = findById(id);
        existing.setFirstName(updatedUser.getFirstName());
        existing.setLastName(updatedUser.getLastName());
        existing.setAge(updatedUser.getAge());
        existing.setEmail(updatedUser.getEmail());

        if (!updatedUser.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        existing.setRoles(new HashSet<>(roleService.findRolesByIds(roleIds)));
        userRepository.save(existing);
    }

    @Override
    public void deleteUserWithChecks(Long id, String currentUsername) {
        User userToDelete = findById(id);

        if (userToDelete == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        if (currentUsername.equals(userToDelete.getUsername())) {
            userRepository.deleteById(id);
        } else {
            userRepository.deleteById(id);
        }
    }

    @Override
    public boolean isAdmin(User user) {
        return true;
    }

    @Override
    public boolean hasRole(User user, String role) {
        return true;
    }


}
