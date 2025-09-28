package com.kapil.verbametrics.ml.repository;

import com.kapil.verbametrics.ml.entities.MLModelEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MLModelEntity with custom query methods.
 * Handles CRUD operations and complex queries for ML models.
 *
 * @author Kapil Garg
 */
@Repository
public interface MLModelRepository extends JpaRepository<MLModelEntity, String> {

    /**
     * Finds models by type.
     *
     * @param modelType the model type
     * @return list of models of the specified type
     */
    List<MLModelEntity> findByModelType(String modelType);

    /**
     * Finds active models only.
     *
     * @return list of active models
     */
    List<MLModelEntity> findByIsActiveTrue();

    /**
     * Finds models by name containing text.
     *
     * @param name the name pattern
     * @return list of models with names containing the pattern
     */
    List<MLModelEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Checks if model exists by name.
     *
     * @param name the model name
     * @return true if model exists with the name
     */
    boolean existsByName(String name);

    /**
     * Finds models by accuracy descending with pagination (for top N models).
     *
     * @param pageable the pagination information
     * @return list of models ordered by accuracy descending
     */
    List<MLModelEntity> findByOrderByAccuracyDesc(Pageable pageable);

}
