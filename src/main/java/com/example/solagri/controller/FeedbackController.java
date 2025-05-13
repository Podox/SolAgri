package com.example.solagri.controller;

import com.example.solagri.dto.AdvancedPredictionInput;
import com.example.solagri.dto.PredictionInput;
import com.example.solagri.dto.PredictionExplanationResponse;
import com.example.solagri.model.AdvancedPrediction;
import com.example.solagri.model.Feedback;
import com.example.solagri.model.Prediction;
import com.example.solagri.model.User;
import com.example.solagri.repository.AdvancedPredictionRepository;
import com.example.solagri.repository.FeedbackRepository;
import com.example.solagri.repository.PredictionRepository;
import com.example.solagri.repository.UserRepository;
import com.example.solagri.service.GroqAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import java.util.List;
@Controller
public class FeedbackController {
    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private AdvancedPredictionRepository advancedPredictionRepository;

    @Autowired
    private GroqAIService groqAIService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedbackRepository FeedbackRepository;

    @PostMapping("/feedback/{predictionId}")
    public String addFeedback(@PathVariable Long predictionId, String feedbackMessage, double rating, Model model) {
        // Fetch prediction
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prediction ID"));

        // Create Feedback
        Feedback feedback = new Feedback(feedbackMessage, rating, prediction);

        // Save to repository
        FeedbackRepository.save(feedback);

        // Return to feedback page or success confirmation
        model.addAttribute("prediction", prediction);
        return "redirect:/feedback/" + predictionId; // Redirect to prediction details page
    }

    @GetMapping("/feedback/{predictionId}")
    public String viewFeedback(@PathVariable Long predictionId, Model model) {
        List<Feedback> feedbacks = FeedbackRepository.findByPredictionId(predictionId);

        model.addAttribute("feedbacks", feedbacks);
        return "feedback"; // A new Thymeleaf view for displaying feedback
    }

    @GetMapping("/feedback/add")
    public String showAddFeedbackPage(@RequestParam Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Fetch the prediction
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prediction ID"));

        model.addAttribute("prediction", prediction);
        model.addAttribute("feedback", new Feedback()); // Empty Feedback object for form binding
        return "add_feedback"; // Maps to add_feedback.html
    }}
