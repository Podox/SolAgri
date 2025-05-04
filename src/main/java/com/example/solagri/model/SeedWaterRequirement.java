package com.example.solagri.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seed_water_requirements")
public class SeedWaterRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seed_type", nullable = false, unique = true)
    private String seedType;

    @Column(name = "water_per_m2", nullable = false)
    private Double waterPerM2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeedType() {
        return seedType;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public Double getWaterPerM2() {
        return waterPerM2;
    }

    public void setWaterPerM2(Double waterPerM2) {
        this.waterPerM2 = waterPerM2;
    }
}
