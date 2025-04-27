package com.example.solagri.repository;

import com.example.solagri.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    // Query to find all predictions for a specific user by user ID
    List<Prediction> findByUserId(Long userId);
}
