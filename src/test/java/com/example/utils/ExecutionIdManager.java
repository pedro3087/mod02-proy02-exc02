package com.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages unique execution IDs for test runs
 * Provides thread-safe execution ID generation and management
 */
public class ExecutionIdManager {
    private static volatile String currentExecutionId = null;
    private static final AtomicLong executionCounter = new AtomicLong(0);
    private static final String EXECUTION_ID_PREFIX = "exec_";
    private static volatile boolean isInitialized = false;
    
    // Static initializer to ensure execution ID is generated when class is first loaded
    static {
        initializeExecutionId();
    }
    
    /**
     * Initialize execution ID for this Maven run
     */
    private static synchronized void initializeExecutionId() {
        if (!isInitialized) {
            currentExecutionId = generateExecutionId();
            isInitialized = true;
            System.out.println("🆔 Maven execution started with ID: " + currentExecutionId);
        }
    }
    
    /**
     * Generate a new unique execution ID
     * Format: exec_YYYYMMDD_HHMMSS_UUID_counter
     */
    public static String generateExecutionId() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        long counter = executionCounter.incrementAndGet();
        
        return String.format("%s%s_%s_%d", EXECUTION_ID_PREFIX, timestamp, uuid, counter);
    }
    
    /**
     * Set the current execution ID for this test run
     * Should be called at the beginning of test execution
     */
    public static void setCurrentExecutionId(String executionId) {
        currentExecutionId = executionId;
        System.out.println("🆔 Execution ID set: " + executionId);
    }
    
    /**
     * Get the current execution ID
     * Always returns the execution ID for this Maven run
     */
    public static String getCurrentExecutionId() {
        // Ensure initialization
        if (!isInitialized) {
            initializeExecutionId();
        }
        return currentExecutionId;
    }
    
    /**
     * Clear the current execution ID
     * Should be called at the end of test execution
     */
    public static void clearCurrentExecutionId() {
        if (currentExecutionId != null) {
            System.out.println("🆔 Execution completed: " + currentExecutionId);
            currentExecutionId = null;
            isInitialized = false;
        }
    }
    
    /**
     * Check if an execution ID is currently active
     */
    public static boolean hasActiveExecution() {
        return currentExecutionId != null;
    }
    
    /**
     * Extract execution ID from screenshot filename
     * Format: step_XX_name_EXECUTION_ID.png
     */
    public static String extractExecutionIdFromFilename(String filename) {
        // Remove .png extension
        String nameWithoutExt = filename.replace(".png", "");
        String[] parts = nameWithoutExt.split("_");
        
        // Look for execution ID pattern (starts with exec_)
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith(EXECUTION_ID_PREFIX.replace("_", ""))) {
                // Reconstruct the execution ID
                StringBuilder executionId = new StringBuilder(parts[i]);
                for (int j = i + 1; j < parts.length; j++) {
                    executionId.append("_").append(parts[j]);
                }
                return executionId.toString();
            }
        }
        
        return "unknown";
    }
    
    /**
     * Check if a filename belongs to the current execution
     */
    public static boolean belongsToCurrentExecution(String filename) {
        String executionId = extractExecutionIdFromFilename(filename);
        return getCurrentExecutionId().equals(executionId);
    }
    
    /**
     * Get execution summary for logging
     */
    public static String getExecutionSummary() {
        if (currentExecutionId != null) {
            return "Current Execution: " + currentExecutionId;
        }
        return "No active execution";
    }
}
