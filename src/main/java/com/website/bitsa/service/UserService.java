package com.website.bitsa.service;


import com.website.bitsa.exception.ResourceNotFoundException;
import com.website.bitsa.model.User;
import com.website.bitsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllStudents() {
        return userRepository.findAllStudents();
    }

    public List<User> getAllAdmins() {
        return userRepository.findAllAdmins();
    }

    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    public long countStudents() {
        return userRepository.countByRoleName("STUDENT");
    }

    public long countAdmins() {
        return userRepository.countByRoleName("ADMIN");
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }
}