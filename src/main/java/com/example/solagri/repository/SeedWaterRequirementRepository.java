package com.example.solagri.repository;

import com.example.solagri.model.SeedWaterRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeedWaterRequirementRepository extends JpaRepository<SeedWaterRequirement, Long> {
    Optional<SeedWaterRequirement> findBySeedType(String seedType);
}