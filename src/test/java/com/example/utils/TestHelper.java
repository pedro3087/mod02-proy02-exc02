package com.example.utils;

import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.TestInfo;

/**
 * Helper class for test utilities including failure handling and screenshot management
 */
public class TestHelper {
    
    /**
     * Setup failure-only screenshot mode for a test
     * Call this at the beginning of your test method
     * 
     * @param enableFailureOnly true to enable failure-only mode, false for all screenshots
     */
    public static void setupScreenshotMode(boolean enableFailureOnly) {
        ScreenshotUtil.setFailureOnlyMode(enableFailureOnly);
        ScreenshotUtil.resetStepCounter();
    }
    
    /**
     * Handle test failure by marking failure and capturing screenshot
     * Use this in catch blocks or when assertions fail
     * 
     * @param driver WebDriver instance
     * @param stepName Name of the step that failed
     * @param exception The exception that occurred
     * @return File path of the failure screenshot
     */
    public static String handleTestFailure(WebDriver driver, String stepName, Exception exception) {
        System.err.println("❌ Test failure in step: " + stepName);
        System.err.println("❌ Exception: " + exception.getMessage());
        
        // Mark failure and capture screenshot
        return ScreenshotUtil.captureFailureScreenshot(driver, stepName);
    }
    
    /**
     * Handle assertion failure by marking failure and capturing screenshot
     * Use this when assertions fail
     * 
     * @param driver WebDriver instance
     * @param stepName Name of the step that failed
     * @param assertionMessage The assertion message
     * @return File path of the failure screenshot
     */
    public static String handleAssertionFailure(WebDriver driver, String stepName, String assertionMessage) {
        System.err.println("❌ Assertion failed in step: " + stepName);
        System.err.println("❌ Message: " + assertionMessage);
        
        // Mark failure and capture screenshot
        return ScreenshotUtil.captureFailureScreenshot(driver, stepName);
    }
    
    /**
     * Capture screenshot with current mode settings
     * 
     * @param driver WebDriver instance
     * @param stepName Name of the step
     * @return File path of the screenshot, or null if not captured
     */
    public static String captureScreenshot(WebDriver driver, String stepName) {
        return ScreenshotUtil.captureScreenshot(driver, stepName);
    }
    
    /**
     * Get current screenshot mode status
     * 
     * @return true if in failure-only mode, false if capturing all screenshots
     */
    public static boolean isFailureOnlyMode() {
        return ScreenshotUtil.isFailureOnlyMode();
    }
    
    /**
     * Check if a failure has occurred in current test
     * 
     * @return true if failure has been marked, false otherwise
     */
    public static boolean hasFailureOccurred() {
        return ScreenshotUtil.hasFailureOccurred();
    }
    
    /**
     * Reset screenshot mode to default (capture all screenshots)
     */
    public static void resetToDefaultMode() {
        ScreenshotUtil.setFailureOnlyMode(false);
        ScreenshotUtil.resetStepCounter();
    }
}
