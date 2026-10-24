package com.rms.restaurant_management_system.user.service;

import com.rms.restaurant_management_system.common.exception.DuplicateLoginException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.user.dto.UserCreateRequest;
import com.rms.restaurant_management_system.user.dto.UserResponse;
import com.rms.restaurant_management_system.user.model.User;
import com.rms.restaurant_management_system.user.model.UserRole;
import com.rms.restaurant_management_system.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return toResponse(user);
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {

        if (userRepository.existsByLogin(request.login())) {
            throw new DuplicateLoginException("Login already exists");
        }

        User user = new User();

        user.setFullName(request.fullName());
        user.setLogin(request.login());

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        user.setRole(request.role());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional
    public UserResponse update(
            Long id,
            UserCreateRequest request
    ) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!user.getLogin().equals(request.login())
                && userRepository.existsByLogin(request.login())) {

            throw new DuplicateLoginException("Login already exists");
        }

        user.setFullName(request.fullName());
        user.setLogin(request.login());
        user.setRole(request.role());

        if (request.password() != null
                && !request.password().isBlank()) {

            user.setPasswordHash(
                    passwordEncoder.encode(request.password())
            );
        }

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        user.setActive(false);
    }

    @Transactional
    public void changeRole(Long id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        user.setRole(role);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getLogin(),
                user.getRole(),
                user.isActive()
        );
    }
}