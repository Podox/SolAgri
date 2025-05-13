package com.example.solagri.repository;

import com.example.solagri.model.AdvancedPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvancedPredictionRepository extends JpaRepository<AdvancedPrediction, Long> {
    AdvancedPrediction findByPredictionId(Long predictionId);
}
