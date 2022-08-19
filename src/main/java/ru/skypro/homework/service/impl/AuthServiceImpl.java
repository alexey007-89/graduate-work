package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegReq;
import ru.skypro.homework.dto.RoleDTO;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.RoleRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsServiceImpl manager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;


    public AuthServiceImpl(UserDetailsServiceImpl manager, UserRepository userRepository, RoleRepository roleRepository) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String userName, String password) {
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        return encoder.matches(password, encryptedPassword);
    }

    @Override
    public boolean register(RegReq regReq, RoleDTO role) {
        return createUser(regReq, role);
    }


    private boolean createUser(RegReq regReq, RoleDTO role) {
        User userFromDB = userRepository.findByUsername(regReq.getUsername());
        if (userFromDB != null) {
            return false;
        }
        User user = new User();
        user.setEmail(regReq.getUsername());
        user.setPassword(encoder.encode(regReq.getPassword()));
        Role userRole = new Role();
        userRole.setRoleName("ROLE_" + role.name());
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
        return true;
    }
}
