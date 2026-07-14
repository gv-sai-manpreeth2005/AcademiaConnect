package org.cse.academiaconnect.service;

import jakarta.persistence.EntityNotFoundException;
import org.cse.academiaconnect.entity.User;
import org.cse.academiaconnect.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class managing User business logic.
 * Uses constructor injection and handles clean CRUD operations.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new User.
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already registered");
        }

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Retrieve all Users.
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieve a User by ID.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    /**
     * Update an existing User's details.
     */
    public User updateUser(Long id, User userDetails) {

        User existingUser = getUserById(id);

        if (!existingUser.getUsername().equals(userDetails.getUsername())
                && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new IllegalArgumentException("Username '" + userDetails.getUsername() + "' is already taken");
        }

        if (!existingUser.getEmail().equals(userDetails.getEmail())
                && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email '" + userDetails.getEmail() + "' is already registered");
        }

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());

        // Encrypt updated password
        existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));

        existingUser.setFullName(userDetails.getFullName());
        existingUser.setRole(userDetails.getRole());
        existingUser.setDepartment(userDetails.getDepartment());

        return userRepository.save(existingUser);
    }

    /**
     * Delete a User by ID.
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}