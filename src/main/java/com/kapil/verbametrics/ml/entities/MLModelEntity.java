package com.kapil.verbametrics.ml.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * JPA Entity for ML Model persistence operations.
 *
 * @author Kapil Garg
 */
@Entity
@Table(name = "ml_models")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLModelEntity {

    @Id
    @Column(name = "model_id", length = 100)
    private String modelId;

    @Column(name = "model_type", nullable = false, length = 50)
    private String modelType;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(name = "model_path", length = 500)
    private String modelPath;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "training_data_size")
    private Integer trainingDataSize;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "status", length = 20)
    private String status;

    @ElementCollection
    @CollectionTable(name = "ml_model_parameters", joinColumns = @JoinColumn(name = "model_id"))
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value")
    private Map<String, String> parameters;

    @ElementCollection
    @CollectionTable(name = "ml_model_metrics", joinColumns = @JoinColumn(name = "model_id"))
    @MapKeyColumn(name = "metric_key")
    @Column(name = "metric_value")
    private Map<String, String> performanceMetrics;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastUsed == null) {
            lastUsed = LocalDateTime.now();
        }
        if (version == null) {
            version = "1.0";
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUsed = LocalDateTime.now();
    }

}
