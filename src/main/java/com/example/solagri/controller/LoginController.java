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
public class LoginController {
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


}
