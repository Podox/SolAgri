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
public class PredictionController {

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
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/predictions")
    public String viewUserPredictions(Model model,
                                      @RequestParam(name = "username", required = false) String username,
                                      HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null && username != null && !username.isEmpty()) {
            user = userRepository.findByUsername(username);
            if (user == null) {
                model.addAttribute("error", "User not found");
                return "login";
            }
            session.setAttribute("user", user);
        }
        if (user == null) {
            return "redirect:/login";
        }
        List<Prediction> predictions = predictionRepository.findByUserId(user.getId());
        model.addAttribute("predictions", predictions);
        model.addAttribute("username", user.getUsername());
        return "predictions"; // Maps to predictions.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Maps to login.html
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        session.setAttribute("user", user);
        return "redirect:/predict";
    }

    @GetMapping("/predict")
    public String showPredictionForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("prediction", new Prediction());
        return "prediction"; // Maps to prediction.html
    }

    @PostMapping("/predict")
    public String makePrediction(Prediction prediction, @RequestParam(required = false) String soilType, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        PredictionInput input = new PredictionInput();
        input.setSeed(prediction.getSeed());
        input.setLandSurface(prediction.getLandSurface());
        input.setWaterDepth(prediction.getWaterDepth());
        input.setWaterTravelingDistance(prediction.getWaterTravelingDistance());
        PredictionExplanationResponse response = groqAIService.predictSolarPanels(input, soilType);
        prediction.setExplanation(response.getExplanation());
        prediction.setKilowatts(response.getKilowatts());
        prediction.setPanels(response.getPanels());
        prediction.setSurfaceArea(response.getSurfaceArea());
        prediction.setCost(response.getCost());
        prediction.setUser(user);
        predictionRepository.save(prediction);
        if (soilType != null && !soilType.isEmpty()) {
            AdvancedPrediction advancedPrediction = new AdvancedPrediction(soilType, prediction);
            advancedPredictionRepository.save(advancedPrediction);
            prediction.setAdvancedPrediction(advancedPrediction);
            predictionRepository.save(prediction);
        }
        // Redirect to /predictions with URL-encoded username
        String redirectUrl = UriComponentsBuilder.fromPath("/predictions")
                .queryParam("username", user.getUsername())
                .build()
                .encode()
                .toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/advanced-predict")
    public String showAdvancedPredictionForm(@RequestParam Long predictionId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Prediction prediction = predictionRepository.findById(predictionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid prediction ID"));
        if (!prediction.getUser().getId().equals(user.getId())) {
            model.addAttribute("error", "Unauthorized access to prediction");
            return "predictions";
        }
        model.addAttribute("prediction", prediction);
        model.addAttribute("advancedPredictionInput", new AdvancedPredictionInput());
        return "advanced_prediction"; // Maps to advanced_prediction.html
    }

    @PostMapping("/advanced-predict")
    public String makeAdvancedPrediction(AdvancedPredictionInput input, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Prediction prediction = predictionRepository.findById(input.getPredictionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid prediction ID"));
        if (!prediction.getUser().getId().equals(user.getId())) {
            model.addAttribute("error", "Unauthorized access to prediction");
            return "predictions";
        }
        PredictionInput predictionInput = new PredictionInput();
        predictionInput.setSeed(prediction.getSeed());
        predictionInput.setLandSurface(prediction.getLandSurface());
        predictionInput.setWaterDepth(prediction.getWaterDepth());
        predictionInput.setWaterTravelingDistance(prediction.getWaterTravelingDistance());
        PredictionExplanationResponse response = groqAIService.predictSolarPanels(predictionInput, input.getSoilType());
        prediction.setExplanation(response.getExplanation());
        prediction.setKilowatts(response.getKilowatts());
        prediction.setPanels(response.getPanels());
        prediction.setSurfaceArea(response.getSurfaceArea());
        prediction.setCost(response.getCost());
        AdvancedPrediction advancedPrediction = new AdvancedPrediction(input.getSoilType(), prediction);
        advancedPredictionRepository.save(advancedPrediction);
        prediction.setAdvancedPrediction(advancedPrediction);
        predictionRepository.save(prediction);
        String redirectUrl = UriComponentsBuilder.fromPath("/predictions")
                .queryParam("username", user.getUsername())
                .build()
                .encode()
                .toUriString();
        return "redirect:" + redirectUrl;
    }
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
        return "redirect:/predictions/" + predictionId; // Redirect to prediction details page
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
    }
}