package com.example.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotUtil {
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static int stepCounter = 1;
    private static boolean failureOnlyMode = false;
    private static boolean hasFailure = false;
    
    /**
     * Set screenshot mode to failure-only
     * @param enabled true to capture screenshots only on failures, false for all steps
     */
    public static void setFailureOnlyMode(boolean enabled) {
        failureOnlyMode = enabled;
    }
    
    /**
     * Mark that a failure has occurred (call this when an assertion fails or exception occurs)
     */
    public static void markFailure() {
        hasFailure = true;
    }
    
    /**
     * Reset failure state (call this at the beginning of each test)
     */
    public static void resetFailureState() {
        hasFailure = false;
    }
    
    /**
     * Check if we should capture a screenshot based on current mode
     * @return true if screenshot should be captured, false otherwise
     */
    private static boolean shouldCaptureScreenshot() {
        return !failureOnlyMode || hasFailure;
    }
    
    public static String captureScreenshot(WebDriver driver, String stepName) {
        // Check if we should capture screenshot based on mode
        if (!shouldCaptureScreenshot()) {
            return null;
        }
        try {
            // Create screenshots directory if it doesn't exist
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
            }
            
            // Generate filename with execution ID and step name
            String executionId = ExecutionIdManager.getCurrentExecutionId();
            String filename = String.format("step_%02d_%s_%s.png", stepCounter++, stepName.replaceAll("[^a-zA-Z0-9]", "_"), executionId);
            String filepath = SCREENSHOT_DIR + "/" + filename;
            
            // Capture screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(filepath);
            
            // Copy file to destination
            Files.copy(sourceFile.toPath(), destFile.toPath());
            
            System.out.println("📸 Screenshot captured: " + filepath);
            return filepath;
            
        } catch (IOException e) {
            System.err.println("❌ Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Capture screenshot specifically for failures
     * @param driver WebDriver instance
     * @param stepName Name of the step that failed
     * @return File path of the screenshot, or null if failed
     */
    public static String captureFailureScreenshot(WebDriver driver, String stepName) {
        // Mark failure and capture screenshot
        markFailure();
        return captureScreenshot(driver, "FAILURE_" + stepName);
    }
    
    /**
     * Reset both step counter and failure state
     */
    public static void resetStepCounter() {
        stepCounter = 1;
        resetFailureState();
    }
    
    /**
     * Get current screenshot mode
     * @return true if in failure-only mode, false if capturing all screenshots
     */
    public static boolean isFailureOnlyMode() {
        return failureOnlyMode;
    }
    
    /**
     * Check if a failure has occurred in current test
     * @return true if failure has been marked, false otherwise
     */
    public static boolean hasFailureOccurred() {
        return hasFailure;
    }
}
