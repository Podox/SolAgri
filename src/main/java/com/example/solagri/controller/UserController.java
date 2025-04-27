//package com.example.solagri.controller;
//
//import com.example.solagri.model.User;
//import com.example.solagri.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.SessionAttribute;
//
//@Controller
//public class UserController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // GET method to display the login page
//    @GetMapping("/login")
//    public String showLoginPage() {
//        return "login"; // The login.html page (create this page in the resources/templates folder)
//    }
//
//    // POST method to handle login
//    @PostMapping("/login")
//    public String handleLogin(@RequestParam String username, @RequestParam String password, Model model) {
//        User user = userRepository.findByUsername(username);
//
//        if (user != null && user.getPassword().equals(password)) {
//            model.addAttribute("user", user);  // Save the logged-in user to the model
//            return "redirect:/predict";  // Redirect to the prediction page
//        }
//
//        model.addAttribute("error", "Invalid username or password.");
//        return "login";  // Return to login page if credentials are incorrect
//    }
//}
