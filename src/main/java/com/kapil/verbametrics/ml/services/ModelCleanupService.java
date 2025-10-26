package com.kapil.verbametrics.ml.services;

/**
 * Service interface for cleaning up model files.
 * Handles both manual cleanup and automatic cleanup on application shutdown.
 * 
 * @author Kapil Garg
 */
public interface ModelCleanupService {
    
    /**
     * Cleans up orphaned model files (files without database entries).
     * This method is called automatically on application shutdown.
     */
    void cleanupOrphanedModelFiles();
    
    /**
     * Cleans up a specific model file by model ID.
     * This method is called when a model is deleted from the database.
     * 
     * @param modelId the ID of the model whose file should be deleted
     * @return true if the file was successfully deleted, false otherwise
     */
    boolean cleanupModelFile(String modelId);

}
