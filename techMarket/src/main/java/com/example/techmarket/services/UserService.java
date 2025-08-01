package com.example.techmarket.services;

import com.example.techmarket.dto.UpdateUserDTO;
import com.example.techmarket.entities.User;
import com.example.techmarket.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author lucio
 */

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> getUserById(int id){
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = false)
    public User save(User user){
        return userRepository.save(user);
    }

    @Transactional(readOnly = false)
    public void changePassword(int userId, String oldPassword, String newPassword){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new IllegalArgumentException("The old password is incorrect");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = false)
    public User updateUserData(int userId, UpdateUserDTO updateUserDTO){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(updateUserDTO.getFirstName() != null) user.setFirstName(updateUserDTO.getFirstName());
        if(updateUserDTO.getLastName() != null) user.setLastName(updateUserDTO.getLastName());
        if(updateUserDTO.getTelephoneNumber() != null) user.setTelephoneNumber(updateUserDTO.getTelephoneNumber());
        if(updateUserDTO.getAddress() != null) user.setAddress(updateUserDTO.getAddress());

        return userRepository.save(user);
    }

    @Transactional(readOnly = false)
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }
}
