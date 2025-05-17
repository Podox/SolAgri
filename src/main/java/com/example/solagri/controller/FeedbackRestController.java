// New REST controller version of your FeedbackController
package com.example.solagri.controller;

import com.example.solagri.model.Feedback;
import com.example.solagri.model.Prediction;
import com.example.solagri.model.User;
import com.example.solagri.repository.FeedbackRepository;
import com.example.solagri.repository.PredictionRepository;
import com.example.solagri.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackRestController {

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /api/feedback/{predictionId}
    @PostMapping("/{predictionId}")
    public ResponseEntity<?> addFeedback(@PathVariable Long predictionId,
                                         @RequestBody Feedback feedback,
                                         HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prediction ID"));

        feedback.setPrediction(prediction);
        feedbackRepository.save(feedback);

        return ResponseEntity.ok(feedback);
    }

    // GET /api/feedback/{predictionId}
    @GetMapping("/{predictionId}")
    public ResponseEntity<?> getFeedbacks(@PathVariable Long predictionId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        List<Feedback> feedbacks = feedbackRepository.findByPredictionId(predictionId);
        return ResponseEntity.ok(feedbacks);
    }
}