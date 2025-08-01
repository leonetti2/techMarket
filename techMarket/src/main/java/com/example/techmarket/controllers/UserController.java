package com.example.techmarket.controllers;

import com.example.techmarket.dto.ChangePasswordRequest;
import com.example.techmarket.dto.UpdateUserDTO;
import com.example.techmarket.entities.User;
import com.example.techmarket.services.UserService;
import com.example.techmarket.support.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lucio
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable int id){
        try {
            var userOptional = userService.getUserById(id);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body(new ResponseMessage("User not found"));
            }
            return ResponseEntity.ok(userOptional.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error retrieving user: " + e.getMessage()));
        }
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email){
        try {
            var userOptional = userService.findByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body(new ResponseMessage("User not found"));
            }
            return ResponseEntity.ok(userOptional.get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("rror retrieving user: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error while retrieving users: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user){
        try {
            var created = userService.save(user);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Error creating user: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody UpdateUserDTO updateUserDTO){
        try {
            var updateUser = userService.updateUserData(id, updateUserDTO);
            return ResponseEntity.ok(updateUser);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ResponseMessage("Error while updating user: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable int id, @RequestBody ChangePasswordRequest request){
        try {
            userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new ResponseMessage("Password changed successfully"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ResponseMessage("Error while changing password: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id){
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseMessage("Error deleting user: " + e.getMessage()));
        }
    }
}
