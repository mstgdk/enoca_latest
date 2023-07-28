package com.enoca.service;

import com.enoca.DTO.request.RegisterRequest;
import com.enoca.domain.Role;
import com.enoca.domain.User;
import com.enoca.domain.enums.RoleTypes;
import com.enoca.exception.ConflictException;
import com.enoca.exception.ResourceNotFoundException;
import com.enoca.exception.message.ErrorMessage;
import com.enoca.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }
    public void saveUser(RegisterRequest registerRequest) {
        // Kullanıcı daha önce kayıtlı mı değil mi kontrol ettik
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE, registerRequest.getEmail()));
        }
        // !!! yeni kullanıcın rol bilgisini default olarak "ADMIN" atıyorum
        Role role = roleService.findByType(RoleTypes.ROLE_ADMIN);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        // Şifre encode etme
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Kullanıcıyı oluşturuyoruz
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);

        user.setRoles(roles);

        userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_EXCEPTION, email)));

        return user;
    }
}
