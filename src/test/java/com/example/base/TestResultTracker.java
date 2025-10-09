package com.example.base;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Thread-safe test result tracker for managing test execution state
 * Tracks test failures, execution times, and provides centralized failure handling
 */
public class TestResultTracker {
    private static final Map<String, TestExecutionInfo> testExecutions = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> currentTestName = new ThreadLocal<>();
    
    /**
     * Test execution information
     */
    public static class TestExecutionInfo {
        private final String testName;
        private final LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean hasFailures = false;
        private Exception lastException;
        private String lastFailureStep;
        private int screenshotCount = 0;
        
        public TestExecutionInfo(String testName) {
            this.testName = testName;
            this.startTime = LocalDateTime.now();
        }
        
        // Getters and setters
        public String getTestName() { return testName; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public boolean hasFailures() { return hasFailures; }
        public void setHasFailures(boolean hasFailures) { this.hasFailures = hasFailures; }
        public Exception getLastException() { return lastException; }
        public void setLastException(Exception lastException) { this.lastException = lastException; }
        public String getLastFailureStep() { return lastFailureStep; }
        public void setLastFailureStep(String lastFailureStep) { this.lastFailureStep = lastFailureStep; }
        public int getScreenshotCount() { return screenshotCount; }
        public void incrementScreenshotCount() { this.screenshotCount++; }
        
        public long getExecutionTimeMs() {
            if (endTime != null) {
                return java.time.Duration.between(startTime, endTime).toMillis();
            }
            return java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
        }
    }
    
    /**
     * Start tracking a test execution
     */
    public static void startTest(String testName) {
        currentTestName.set(testName);
        testExecutions.put(testName, new TestExecutionInfo(testName));
        System.out.println("🚀 Starting test: " + testName);
    }
    
    /**
     * End tracking a test execution
     */
    public static void endTest(String testName) {
        TestExecutionInfo info = testExecutions.get(testName);
        if (info != null) {
            info.setEndTime(LocalDateTime.now());
            System.out.println("✅ Completed test: " + testName + 
                " (Duration: " + info.getExecutionTimeMs() + "ms, Screenshots: " + info.getScreenshotCount() + ")");
        }
        currentTestName.remove();
    }
    
    /**
     * Record a test failure
     */
    public static void recordFailure(String testName, String stepName, Exception exception) {
        TestExecutionInfo info = testExecutions.get(testName);
        if (info != null) {
            info.setHasFailures(true);
            info.setLastException(exception);
            info.setLastFailureStep(stepName);
            System.out.println("❌ Test failure recorded: " + testName + " - Step: " + stepName);
        }
    }
    
    /**
     * Record a screenshot capture
     */
    public static void recordScreenshot(String testName) {
        TestExecutionInfo info = testExecutions.get(testName);
        if (info != null) {
            info.incrementScreenshotCount();
        }
    }
    
    /**
     * Get current test name for the current thread
     */
    public static String getCurrentTestName() {
        return currentTestName.get();
    }
    
    /**
     * Get test execution info
     */
    public static TestExecutionInfo getTestInfo(String testName) {
        return testExecutions.get(testName);
    }
    
    /**
     * Check if current test has failures
     */
    public static boolean hasCurrentTestFailures() {
        String currentTest = getCurrentTestName();
        if (currentTest != null) {
            TestExecutionInfo info = testExecutions.get(currentTest);
            return info != null && info.hasFailures();
        }
        return false;
    }
    
    /**
     * Get summary of all test executions
     */
    public static String getExecutionSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n📊 Test Execution Summary:\n");
        summary.append("=" .repeat(80)).append("\n");
        
        int totalTests = testExecutions.size();
        long totalFailures = testExecutions.values().stream().mapToLong(info -> info.hasFailures() ? 1 : 0).sum();
        long totalScreenshots = testExecutions.values().stream().mapToLong(TestExecutionInfo::getScreenshotCount).sum();
        
        summary.append("Total Tests: ").append(totalTests).append("\n");
        summary.append("Failed Tests: ").append(totalFailures).append("\n");
        summary.append("Passed Tests: ").append(totalTests - totalFailures).append("\n");
        summary.append("Total Screenshots: ").append(totalScreenshots).append("\n");
        summary.append("=" .repeat(80)).append("\n");
        
        for (TestExecutionInfo info : testExecutions.values()) {
            String status = info.hasFailures() ? "❌ FAILED" : "✅ PASSED";
            String failureDetails = "";
            
            if (info.hasFailures()) {
                failureDetails = String.format(" | Step: %s | Exception: %s", 
                    info.getLastFailureStep() != null ? info.getLastFailureStep() : "Unknown",
                    info.getLastException() != null ? info.getLastException().getClass().getSimpleName() : "Unknown");
            }
            
            summary.append(String.format("%-40s | %s | %dms | %d screenshots%s\n",
                info.getTestName(),
                status,
                info.getExecutionTimeMs(),
                info.getScreenshotCount(),
                failureDetails
            ));
        }
        
        return summary.toString();
    }
    
    /**
     * Get detailed failure information for a specific test
     */
    public static String getDetailedFailureInfo(String testName) {
        TestExecutionInfo info = testExecutions.get(testName);
        if (info == null || !info.hasFailures()) {
            return "No failure information available for test: " + testName;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("\n🔍 Detailed Failure Information:\n");
        details.append("=" .repeat(60)).append("\n");
        details.append("Test Name: ").append(info.getTestName()).append("\n");
        details.append("Failure Step: ").append(info.getLastFailureStep()).append("\n");
        details.append("Exception Type: ").append(info.getLastException().getClass().getSimpleName()).append("\n");
        details.append("Exception Message: ").append(info.getLastException().getMessage()).append("\n");
        details.append("Execution Time: ").append(info.getExecutionTimeMs()).append("ms\n");
        details.append("Screenshots Captured: ").append(info.getScreenshotCount()).append("\n");
        details.append("=" .repeat(60)).append("\n");
        
        return details.toString();
    }
    
    /**
     * Clear all test execution data
     */
    public static void clearAll() {
        testExecutions.clear();
        currentTestName.remove();
    }
}
