package com.example.solagri.controller;

import com.example.solagri.dto.UserDTO;
import com.example.solagri.model.User;
import com.example.solagri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody Map<String, String> loginData, HttpSession session) {
        String usernameOrEmail = loginData.get("email");
        String password = loginData.get("password");

        User user;
        if (usernameOrEmail.contains("@")) {
            user = userRepository.findByEmail(usernameOrEmail);
        } else {
            user = userRepository.findByUsername(usernameOrEmail);
        }

        if (user == null || !user.getPassword().equals(password)) {

            System.err.println("Login unsuccessful");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email/username or password"));
        }

        session.setAttribute("user", user);

        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail());
        System.err.println("Login successful");
        return ResponseEntity.ok(Map.of("message", "Login successful", "user", userDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String email = data.get("email");
        String password = data.get("password");

        if (userRepository.findByUsername(username) != null || userRepository.findByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username or email already exists"));
        }

        User user = new User(username, password, email);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Registration successful"));
    }

}
