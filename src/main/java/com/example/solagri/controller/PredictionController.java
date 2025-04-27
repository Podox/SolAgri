package com.example.solagri.controller;

import com.example.solagri.dto.PredictionInput;
import com.example.solagri.dto.PredictionExplanationResponse;
import com.example.solagri.model.Prediction;
import com.example.solagri.model.User;
import com.example.solagri.repository.PredictionRepository;
import com.example.solagri.repository.UserRepository;
import com.example.solagri.service.GroqAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    private GroqAIService groqAIService;

    @Autowired
    private UserRepository userRepository;

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
    public String makePrediction(Prediction prediction, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        PredictionInput input = new PredictionInput();
        input.setSeed(prediction.getSeed());
        input.setLandSurface(prediction.getLandSurface());
        input.setWaterDepth(prediction.getWaterDepth());
        input.setWaterTravelingDistance(prediction.getWaterTravelingDistance());
        PredictionExplanationResponse response = groqAIService.predictSolarPanels(input);
        prediction.setExplanation(response.getExplanation());
        prediction.setKilowatts(response.getKilowatts());
        prediction.setPanels(response.getPanels());
        prediction.setSurfaceArea(response.getSurfaceArea());
        prediction.setCost(response.getCost());
        prediction.setUser(user);
        predictionRepository.save(prediction);
        // Redirect to /predictions with URL-encoded username
        String redirectUrl = UriComponentsBuilder.fromPath("/predictions")
                .queryParam("username", user.getUsername())
                .build()
                .encode()
                .toUriString();
        return "redirect:" + redirectUrl;
    }
}