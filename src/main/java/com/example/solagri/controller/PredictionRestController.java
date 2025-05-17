package com.example.solagri.controller;

import com.example.solagri.dto.AdvancedPredictionInput;
import com.example.solagri.dto.PredictionInput;
import com.example.solagri.dto.PredictionExplanationResponse;
import com.example.solagri.model.AdvancedPrediction;
import com.example.solagri.model.Prediction;
import com.example.solagri.model.User;
import com.example.solagri.repository.AdvancedPredictionRepository;
import com.example.solagri.repository.PredictionRepository;
import com.example.solagri.repository.UserRepository;
import com.example.solagri.service.GroqAIService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PredictionRestController {

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private AdvancedPredictionRepository advancedPredictionRepository;

    @Autowired
    private GroqAIService groqAIService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get all predictions for logged-in user
    @GetMapping("/predictions")
    public ResponseEntity<?> getUserPredictions(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        List<Prediction> predictions = predictionRepository.findByUserId(user.getId());
        return ResponseEntity.ok(predictions);
    }

    // ✅ Submit a new prediction (with optional soil type)
    @PostMapping("/predict")
    public ResponseEntity<?> makePrediction(@RequestBody PredictionInput input,
                                            @RequestParam(required = false) String soilType,
                                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        PredictionExplanationResponse response = groqAIService.predictSolarPanels(input, soilType);

        Prediction prediction = new Prediction();
        prediction.setSeed(input.getSeed());
        prediction.setLandSurface(input.getLandSurface());
        prediction.setWaterDepth(input.getWaterDepth());
        prediction.setWaterTravelingDistance(input.getWaterTravelingDistance());
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

        return ResponseEntity.ok(prediction);
    }

    @PostMapping("/advanced-predict")
    public ResponseEntity<?> updatePredictionWithSoil(@RequestBody AdvancedPredictionInput input, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not authenticated");

        Prediction prediction = predictionRepository.findById(input.getPredictionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid prediction ID"));

        if (!prediction.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Unauthorized access");
        }

        PredictionInput baseInput = new PredictionInput();
        baseInput.setSeed(prediction.getSeed());
        baseInput.setLandSurface(prediction.getLandSurface());
        baseInput.setWaterDepth(prediction.getWaterDepth());
        baseInput.setWaterTravelingDistance(prediction.getWaterTravelingDistance());

        PredictionExplanationResponse response = groqAIService.predictSolarPanels(baseInput, input.getSoilType());

        prediction.setExplanation(response.getExplanation());
        prediction.setKilowatts(response.getKilowatts());
        prediction.setPanels(response.getPanels());
        prediction.setSurfaceArea(response.getSurfaceArea());
        prediction.setCost(response.getCost());

        AdvancedPrediction existingAdvanced = prediction.getAdvancedPrediction();
        if (existingAdvanced != null) {
            // ✅ Update existing object
            existingAdvanced.setSoilType(input.getSoilType());
            advancedPredictionRepository.save(existingAdvanced);
        } else {
            // ✅ First-time creation
            AdvancedPrediction newAdvanced = new AdvancedPrediction(input.getSoilType(), prediction);
            advancedPredictionRepository.save(newAdvanced);
            prediction.setAdvancedPrediction(newAdvanced);
        }

        predictionRepository.save(prediction);
        return ResponseEntity.ok(prediction);
    }

}
