package com.example.techmarket.controllers;

import com.example.techmarket.support.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.techmarket.authentication.AuthRequest;
import com.example.techmarket.authentication.AuthResponse;
import com.example.techmarket.entities.Role;
import com.example.techmarket.entities.User;
import com.example.techmarket.repositories.UserRepository;
import com.example.techmarket.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ResponseMessage("Email already registered"));
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(Role.USER);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setTelephoneNumber(request.getTelephoneNumber());
            user.setAddress(request.getAddress());

            userRepository.save(user);
            return ResponseEntity.ok(new ResponseMessage("Registration successful"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ResponseMessage("Error while registering: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            var userOptional = userRepository.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404)
                        .body(new ResponseMessage("User not found"));
            }
            var user = userOptional.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401)
                        .body(new ResponseMessage("Invalid credentials"));
            }
            String token = jwtUtil.generateToken(request.getEmail(), user.getRole().toString());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new ResponseMessage("Invalid credentials or authentication error: " + e.getMessage()));
        }
    }
}
