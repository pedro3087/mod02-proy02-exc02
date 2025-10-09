package com.example.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Base64;

public class ReportGenerator {
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String REPORT_DIR = "target/reports";
    private static final String REPORT_FILE = "target/reports/test-report-with-screenshots.html";
    
    public static void generateReport() {
        try {
            // Create reports directory
            Files.createDirectories(Paths.get(REPORT_DIR));
            
            // Only clean up old screenshots at the very beginning of execution
            // Don't clean up during the execution to preserve screenshots from the same run
            if (shouldCleanupScreenshots()) {
                organizeScreenshotsByExecution();
            }
            
            // Generate HTML report
            String htmlContent = generateHtmlContent();
            
            // Write HTML file
            Files.write(Paths.get(REPORT_FILE), htmlContent.getBytes());
            
            System.out.println("📊 Beautiful HTML report with screenshots generated: " + REPORT_FILE);
            
        } catch (IOException e) {
            System.err.println("❌ Failed to generate report: " + e.getMessage());
        }
    }
    
    /**
     * Determine if screenshots should be cleaned up
     * Only clean up at the very beginning of a Maven execution
     */
    private static boolean shouldCleanupScreenshots() {
        // Only clean up if this is the first time we're generating a report
        // This prevents cleaning up screenshots from the same execution
        return !hasGeneratedReport();
    }
    
    /**
     * Check if a report has already been generated in this execution
     */
    private static boolean hasGeneratedReport() {
        return Files.exists(Paths.get(REPORT_FILE));
    }
    
    /**
     * Clean up all old screenshots before starting a new test execution
     * This should be called at the beginning of test runs
     */
    public static void cleanAllOldScreenshots() {
        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                return;
            }
            
            File[] allScreenshots = screenshotDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (allScreenshots == null || allScreenshots.length == 0) {
                return;
            }
            
            int deletedCount = 0;
            for (File screenshot : allScreenshots) {
                if (screenshot.delete()) {
                    deletedCount++;
                }
            }
            
            System.out.println("🧹 Cleaned up " + deletedCount + " old screenshots before new test execution");
            
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to clean old screenshots: " + e.getMessage());
        }
    }
    
    /**
     * Clean up old screenshots and organize by test execution
     */
    private static void organizeScreenshotsByExecution() throws IOException {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            return;
        }
        
        // Get all PNG files
        File[] allScreenshots = screenshotDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (allScreenshots == null || allScreenshots.length == 0) {
            return;
        }
        
        // Group screenshots by test execution (based on execution ID in filename)
        Map<String, List<File>> executionGroups = new HashMap<>();
        
        for (File screenshot : allScreenshots) {
            String filename = screenshot.getName();
            String executionId = ExecutionIdManager.extractExecutionIdFromFilename(filename);
            executionGroups.computeIfAbsent(executionId, k -> new ArrayList<>()).add(screenshot);
        }
        
        // Get current execution ID
        String currentExecutionId = ExecutionIdManager.getCurrentExecutionId();
        
        // Only keep screenshots from the current execution
        // Delete all screenshots from previous test runs (different execution IDs)
        int deletedCount = 0;
        for (Map.Entry<String, List<File>> entry : executionGroups.entrySet()) {
            String executionId = entry.getKey();
            
            // Only delete screenshots from different execution IDs
            // Keep all screenshots from the current execution (same execution ID)
            if (!currentExecutionId.equals(executionId)) {
                for (File oldScreenshot : entry.getValue()) {
                    if (oldScreenshot.delete()) {
                        deletedCount++;
                    }
                }
            }
        }
        
        if (deletedCount > 0) {
            System.out.println("🧹 Cleaned up " + deletedCount + " old screenshots from previous test runs");
        } else {
            System.out.println("📸 Keeping all screenshots from current execution: " + currentExecutionId);
        }
    }
    
    
    /**
     * Data class to hold test execution statistics
     */
    private static class TestExecutionData {
        int totalTests;
        int failures;
        int errors;
        int totalScreenshots;
        boolean hasFailures;
        
        TestExecutionData(int totalTests, int failures, int errors, int totalScreenshots) {
            this.totalTests = totalTests;
            this.failures = failures;
            this.errors = errors;
            this.totalScreenshots = totalScreenshots;
            this.hasFailures = failures > 0 || errors > 0;
        }
    }
    
    /**
     * Get test execution data from screenshots
     */
    private static TestExecutionData getTestExecutionData() {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            return new TestExecutionData(0, 0, 0, 0);
        }
        
        File[] screenshots = screenshotDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (screenshots == null) {
            return new TestExecutionData(0, 0, 0, 0);
        }
        
        int totalScreenshots = screenshots.length;
        int failures = 0;
        int errors = 0;
        
        // Count failures and errors based on screenshot names
        for (File screenshot : screenshots) {
            String filename = screenshot.getName().toLowerCase();
            if (filename.contains("failure")) {
                failures++;
            }
        }
        
        // Estimate test count based on scenario grouping
        Map<String, List<File>> scenarioGroups = groupScreenshotsByScenario();
        int totalTests = scenarioGroups.size();
        
        return new TestExecutionData(totalTests, failures, errors, totalScreenshots);
    }
    
    /**
     * Group screenshots by test scenario
     */
    private static Map<String, List<File>> groupScreenshotsByScenario() {
        File screenshotDir = new File(SCREENSHOT_DIR);
        Map<String, List<File>> scenarioGroups = new LinkedHashMap<>();
        
        if (!screenshotDir.exists()) {
            return scenarioGroups;
        }
        
        File[] screenshots = screenshotDir.listFiles((dir, name) -> name.endsWith(".png"));
        if (screenshots == null) {
            return scenarioGroups;
        }
        
        // Sort screenshots by filename to maintain order
        Arrays.sort(screenshots);
        
        for (File screenshot : screenshots) {
            String scenarioName = getScenarioName(screenshot.getName());
            scenarioGroups.computeIfAbsent(scenarioName, k -> new ArrayList<>()).add(screenshot);
        }
        
        return scenarioGroups;
    }
    
    /**
     * Extract scenario name from screenshot filename
     */
    private static String getScenarioName(String filename) {
        // Extract test scenario from filename
        if (filename.contains("Inventory_Flow")) {
            return "E-commerce Inventory Flow Test";
        } else if (filename.contains("testWindows")) {
            return "Window Handling Test";
        } else if (filename.contains("testAlerts")) {
            return "JavaScript Alerts Test";
        } else if (filename.contains("testFrames")) {
            return "iFrame Handling Test";
        } else if (filename.contains("testFormAutomation")) {
            return "Form Automation Test";
        } else if (filename.contains("testFailureOnlyScreenshots")) {
            return "Failure-Only Screenshot Demo";
        } else if (filename.contains("FAILURE_")) {
            return "Test Failure Scenarios";
        }
        return "Unknown Test Scenario";
    }
    
    /**
     * Check if scenario has failures
     */
    private static boolean hasFailureInScenario(List<File> screenshots) {
        return screenshots.stream()
            .anyMatch(file -> file.getName().toLowerCase().contains("failure"));
    }
    
    private static String generateHtmlContent() {
        StringBuilder html = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Inventory Flow Test Report</title>\n");
        html.append("    <style>\n");
        html.append(getCssStyles());
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // Get test execution data
        TestExecutionData executionData = getTestExecutionData();
        
        // Header
        html.append("    <div class=\"header\">\n");
        html.append("        <h1>🧪 Selenium Test Execution Report</h1>\n");
        html.append("        <p class=\"timestamp\">Generated on: ").append(timestamp).append("</p>\n");
        html.append("        <div class=\"status-badge ").append(executionData.hasFailures ? "failure" : "success").append("\">")
             .append(executionData.hasFailures ? "❌ SOME TESTS FAILED" : "✅ ALL TESTS PASSED").append("</div>\n");
        html.append("    </div>\n");
        
        // Summary
        html.append("    <div class=\"summary\">\n");
        html.append("        <h2>📊 Test Summary</h2>\n");
        html.append("        <div class=\"stats\">\n");
        html.append("            <div class=\"stat\"><span class=\"number\">").append(executionData.totalTests).append("</span><span class=\"label\">Tests Run</span></div>\n");
        html.append("            <div class=\"stat\"><span class=\"number\">").append(executionData.failures).append("</span><span class=\"label\">Failures</span></div>\n");
        html.append("            <div class=\"stat\"><span class=\"number\">").append(executionData.errors).append("</span><span class=\"label\">Errors</span></div>\n");
        html.append("            <div class=\"stat\"><span class=\"number\">").append(executionData.totalScreenshots).append("</span><span class=\"label\">Screenshots</span></div>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        
        // Test Scenarios
        html.append("    <div class=\"test-scenarios\">\n");
        html.append("        <h2>🔍 Test Scenarios</h2>\n");
        
        // Group screenshots by test scenario
        Map<String, List<File>> scenarioGroups = groupScreenshotsByScenario();
        
        int scenarioNumber = 1;
        for (Map.Entry<String, List<File>> scenario : scenarioGroups.entrySet()) {
            String scenarioName = scenario.getKey();
            List<File> screenshots = scenario.getValue();
            
            // Extract test context information from failure screenshots
            TestContextInfo testContext = null;
            for (File screenshot : screenshots) {
                String stepName = getStepName(screenshot.getName());
                TestContextInfo context = getTestContextInfo(stepName);
                if (context.isDetailedFailure) {
                    testContext = context;
                    break; // Use the first detailed failure context found
                }
            }
            
            html.append("        <div class=\"scenario\">\n");
            html.append("            <div class=\"scenario-header\">\n");
            html.append("                <div class=\"scenario-title-row\">\n");
            html.append("                    <h3>Scenario ").append(scenarioNumber).append(": ").append(scenarioName).append("</h3>\n");
            html.append("                    <span class=\"scenario-status ").append(hasFailureInScenario(screenshots) ? "failure" : "success").append("\">")
                 .append(hasFailureInScenario(screenshots) ? "❌ FAILED" : "✅ PASSED").append("</span>\n");
            html.append("                </div>\n");
            
            // Add test context information if available
            if (testContext != null && testContext.isDetailedFailure) {
                html.append("                <div class=\"test-context\">\n");
                html.append("                    <div class=\"test-info\">\n");
                html.append("                        <span class=\"test-class\">📁 Test Class: <strong>").append(testContext.testClass).append("</strong></span>\n");
                html.append("                        <span class=\"test-method\">🔧 Test Method: <strong>").append(testContext.testMethod).append("()</strong></span>\n");
                html.append("                        <span class=\"exception-type\">⚠️ Exception: <strong>").append(testContext.exceptionType).append("</strong></span>\n");
                html.append("                    </div>\n");
                html.append("                </div>\n");
            }
            
            html.append("            </div>\n");
            html.append("            <div class=\"scenario-steps\">\n");
            
            for (int i = 0; i < screenshots.size(); i++) {
                File screenshot = screenshots.get(i);
                String stepNumber = String.format("%02d", i + 1);
                String stepName = getStepName(screenshot.getName());
                String base64Image = encodeImageToBase64(screenshot);
                boolean isFailure = stepName.toLowerCase().contains("failure");
                
                html.append("                <div class=\"step ").append(isFailure ? "failure-step" : "").append("\">\n");
                html.append("                    <div class=\"step-header\">\n");
                html.append("                        <h4>Step ").append(stepNumber).append(": ").append(stepName).append("</h4>\n");
                html.append("                        <span class=\"step-status ").append(isFailure ? "failure" : "success").append("\">")
                     .append(isFailure ? "❌ FAILED" : "✅ PASSED").append("</span>\n");
                html.append("                    </div>\n");
                html.append("                    <div class=\"step-content\">\n");
                html.append("                        <div class=\"screenshot-container\">\n");
                html.append("                            <img src=\"data:image/png;base64,").append(base64Image).append("\" alt=\"Step ").append(stepNumber).append(" Screenshot\" class=\"screenshot\">\n");
                html.append("                        </div>\n");
                html.append("                        <div class=\"step-description\">\n");
                html.append("                            <p>").append(getStepDescription(stepName, scenarioName)).append("</p>\n");
                html.append("                        </div>\n");
                html.append("                    </div>\n");
                html.append("                </div>\n");
            }
            
            html.append("            </div>\n");
            html.append("        </div>\n");
            scenarioNumber++;
        }
        
        html.append("    </div>\n");
        
        // Footer
        html.append("    <div class=\"footer\">\n");
        html.append("        <p>Generated by Selenium WebDriver Test Automation</p>\n");
        html.append("        <p>Browser: Chrome | Framework: JUnit 5 | Build Tool: Maven</p>\n");
        html.append("    </div>\n");
        
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    private static String getCssStyles() {
        return """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                color: #333;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
            }
            
            .header {
                background: rgba(255, 255, 255, 0.95);
                padding: 2rem;
                text-align: center;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                margin-bottom: 2rem;
            }
            
            .header h1 {
                color: #2c3e50;
                font-size: 2.5rem;
                margin-bottom: 0.5rem;
            }
            
            .timestamp {
                color: #7f8c8d;
                font-size: 1.1rem;
            }
            
            .status-badge {
                display: inline-block;
                padding: 0.5rem 1rem;
                border-radius: 25px;
                font-weight: bold;
                margin-top: 1rem;
            }
            
            .status-badge.success {
                background: #27ae60;
                color: white;
            }
            
            .status-badge.failure {
                background: #e74c3c;
                color: white;
            }
            
            .test-scenarios {
                background: rgba(255, 255, 255, 0.95);
                margin: 0 2rem 2rem 2rem;
                padding: 2rem;
                border-radius: 15px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            
            .scenario {
                margin-bottom: 2rem;
                border: 1px solid #e0e0e0;
                border-radius: 10px;
                overflow: hidden;
            }
            
            .scenario-header {
                background: linear-gradient(135deg, #3498db, #2980b9);
                color: white;
                padding: 1rem 1.5rem;
                display: flex;
                flex-direction: column;
                gap: 10px;
            }
            
            .scenario-title-row {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            
            .scenario-header h3 {
                margin: 0;
                font-size: 1.3rem;
            }
            
            .scenario-status {
                padding: 0.3rem 0.8rem;
                border-radius: 15px;
                font-size: 0.9rem;
                font-weight: bold;
            }
            
            .scenario-status.success {
                background: rgba(39, 174, 96, 0.2);
                color: #27ae60;
                border: 1px solid #27ae60;
            }
            
            .scenario-status.failure {
                background: rgba(231, 76, 60, 0.2);
                color: #e74c3c;
                border: 1px solid #e74c3c;
            }
            
            .test-context {
                margin-top: 10px;
                width: 100%;
            }
            
            .test-info {
                display: flex;
                flex-wrap: wrap;
                gap: 15px;
                margin-top: 8px;
            }
            
            .test-info span {
                background: rgba(255, 255, 255, 0.2);
                padding: 5px 10px;
                border-radius: 15px;
                font-size: 0.9em;
                backdrop-filter: blur(10px);
            }
            
            .test-class {
                border-left: 3px solid #4CAF50;
            }
            
            .test-method {
                border-left: 3px solid #2196F3;
            }
            
            .exception-type {
                border-left: 3px solid #FF9800;
            }
            
            .scenario-steps {
                padding: 1rem;
                background: #f8f9fa;
            }
            
            .failure-step {
                border-left: 4px solid #e74c3c;
                background: #fdf2f2;
            }
            
            .step-status.failure {
                background: #e74c3c;
                color: white;
            }
            
            .summary {
                background: rgba(255, 255, 255, 0.95);
                margin: 0 2rem 2rem 2rem;
                padding: 2rem;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            
            .summary h2 {
                color: #2c3e50;
                margin-bottom: 1.5rem;
                font-size: 1.8rem;
            }
            
            .stats {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 1rem;
            }
            
            .stat {
                text-align: center;
                padding: 1rem;
                background: #f8f9fa;
                border-radius: 8px;
                border-left: 4px solid #3498db;
            }
            
            .stat .number {
                display: block;
                font-size: 2rem;
                font-weight: bold;
                color: #3498db;
            }
            
            .stat .label {
                color: #7f8c8d;
                font-size: 0.9rem;
            }
            
            .test-steps {
                background: rgba(255, 255, 255, 0.95);
                margin: 0 2rem 2rem 2rem;
                padding: 2rem;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            
            .test-steps h2 {
                color: #2c3e50;
                margin-bottom: 2rem;
                font-size: 1.8rem;
            }
            
            .step {
                margin-bottom: 2rem;
                border: 1px solid #e9ecef;
                border-radius: 8px;
                overflow: hidden;
                background: #fff;
            }
            
            .step-header {
                background: #f8f9fa;
                padding: 1rem 1.5rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #e9ecef;
            }
            
            .step-header h3 {
                color: #2c3e50;
                font-size: 1.2rem;
            }
            
            .step-status {
                padding: 0.3rem 0.8rem;
                border-radius: 15px;
                font-size: 0.9rem;
                font-weight: bold;
            }
            
            .step-status.success {
                background: #d4edda;
                color: #155724;
            }
            
            .step-content {
                padding: 1.5rem;
            }
            
            .screenshot-container {
                text-align: center;
                margin-bottom: 1rem;
            }
            
            .screenshot {
                max-width: 100%;
                height: auto;
                border: 2px solid #e9ecef;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                transition: transform 0.3s ease;
            }
            
            .screenshot:hover {
                transform: scale(1.02);
            }
            
            .step-description {
                background: #f8f9fa;
                padding: 1rem;
                border-radius: 6px;
                border-left: 4px solid #3498db;
            }
            
            .step-description p {
                color: #495057;
                margin: 0;
            }
            
            .footer {
                background: rgba(255, 255, 255, 0.95);
                margin: 0 2rem 2rem 2rem;
                padding: 1.5rem;
                text-align: center;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            }
            
            .footer p {
                color: #7f8c8d;
                margin: 0.5rem 0;
            }
            
            @media (max-width: 768px) {
                .header h1 {
                    font-size: 2rem;
                }
                
                .summary, .test-steps, .footer {
                    margin: 0 1rem 1rem 1rem;
                }
                
                .stats {
                    grid-template-columns: repeat(2, 1fr);
                }
            }
            """;
    }
    
    private static String getStepName(String filename) {
        // Extract step name from filename
        // Format: step_XX_FAILURE_ClassName_MethodName_ExceptionType_Failure_YYYYMMDD_HHMMSS.png
        String[] parts = filename.split("_");
        if (parts.length >= 3) {
            // Remove the .png extension first
            String nameWithoutExt = filename.replace(".png", "");
            String[] nameParts = nameWithoutExt.split("_");
            
            if (nameParts.length >= 6) {
                // For detailed failure names, return the full context
                // Skip "step", "XX", "FAILURE" and take everything until the timestamp
                StringBuilder stepName = new StringBuilder();
                for (int i = 3; i < nameParts.length - 2; i++) { // Skip last 2 parts (timestamp)
                    if (i > 3) stepName.append("_");
                    stepName.append(nameParts[i]);
                }
                return stepName.toString();
            } else {
                // For simple step names, return the 3rd part
                return parts[2].replace("_", " ");
            }
        }
        return "Unknown Step";
    }
    
    /**
     * Extract test class and method information from step name
     */
    private static TestContextInfo getTestContextInfo(String stepName) {
        TestContextInfo context = new TestContextInfo();
        
        // Check if this is a detailed failure step name (format: ClassName_MethodName_ExceptionType_Failure)
        if (stepName.contains("_") && stepName.contains("Failure")) {
            String[] parts = stepName.split("_");
            if (parts.length >= 4) {
                // Find the last occurrence of "Failure" to separate the context from the exception
                int failureIndex = -1;
                for (int i = parts.length - 1; i >= 0; i--) {
                    if (parts[i].equals("Failure")) {
                        failureIndex = i;
                        break;
                    }
                }
                
                if (failureIndex >= 3) {
                    context.testClass = parts[0];
                    context.testMethod = parts[1];
                    context.exceptionType = parts[failureIndex - 1];
                    context.isDetailedFailure = true;
                }
            }
        }
        
        return context;
    }
    
    /**
     * Test context information
     */
    private static class TestContextInfo {
        String testClass = "Unknown";
        String testMethod = "Unknown";
        String exceptionType = "Unknown";
        boolean isDetailedFailure = false;
    }
    
    private static String getStepDescription(String stepName, String scenarioName) {
        // Handle failure scenarios
        if (stepName.toLowerCase().contains("failure")) {
            return "Test failure occurred at this step. Screenshot captured for debugging purposes.";
        }
        
        // Handle specific scenarios
        switch (scenarioName) {
            case "E-commerce Inventory Flow Test":
                return getInventoryFlowDescription(stepName);
            case "Window Handling Test":
                return getWindowHandlingDescription(stepName);
            case "JavaScript Alerts Test":
                return getAlertsDescription(stepName);
            case "iFrame Handling Test":
                return getFramesDescription(stepName);
            case "Form Automation Test":
                return getFormAutomationDescription(stepName);
            case "Failure-Only Screenshot Demo":
                return getFailureDemoDescription(stepName);
            default:
                return "Test step executed successfully with screenshot captured.";
        }
    }
    
    private static String getInventoryFlowDescription(String stepName) {
        switch (stepName.toLowerCase()) {
            case "01 login page":
                return "Initial login page loaded successfully. User can see the login form with username and password fields.";
            case "02 credentials entered":
                return "Login credentials (standard_user / secret_sauce) have been entered into the form fields.";
            case "03 inventory page loaded":
                return "Successfully logged in and navigated to the inventory page. Product list is visible.";
            case "04 before add to cart click":
                return "Located the first 'Add to cart' button. Ready to add product to shopping cart.";
            case "05 after add to cart click":
                return "Successfully clicked 'Add to cart' button. Button text changed to 'Remove' indicating item was added.";
            case "06 cart badge shows 1":
                return "Shopping cart badge now displays '1', confirming the item was successfully added to cart.";
            case "07 product detail page":
                return "Navigated to the product detail page. Product information and details are visible.";
            case "08 returned to inventory":
                return "Successfully navigated back to the inventory page using browser back button.";
            case "09 final cart verification":
                return "Final verification: Cart badge still shows '1', confirming cart state persistence across navigation.";
            default:
                return "E-commerce flow test step executed successfully.";
        }
    }
    
    private static String getWindowHandlingDescription(String stepName) {
        return "Window handling test step: " + stepName.replace("_", " ").toLowerCase();
    }
    
    private static String getAlertsDescription(String stepName) {
        return "JavaScript alerts test step: " + stepName.replace("_", " ").toLowerCase();
    }
    
    private static String getFramesDescription(String stepName) {
        return "iFrame handling test step: " + stepName.replace("_", " ").toLowerCase();
    }
    
    private static String getFormAutomationDescription(String stepName) {
        return "Form automation test step: " + stepName.replace("_", " ").toLowerCase();
    }
    
    private static String getFailureDemoDescription(String stepName) {
        if (stepName.toLowerCase().contains("page loaded")) {
            return "Form page loaded successfully. No screenshot captured (failure-only mode).";
        } else if (stepName.toLowerCase().contains("form filled")) {
            return "Form fields filled successfully. No screenshot captured (failure-only mode).";
        } else if (stepName.toLowerCase().contains("element not found")) {
            return "Intentionally triggered failure by looking for non-existent element. Screenshot captured.";
        } else if (stepName.toLowerCase().contains("after failure")) {
            return "Screenshot captured after failure occurred (failure-only mode active).";
        }
        return "Failure demonstration test step.";
    }
    
    private static String encodeImageToBase64(File imageFile) {
        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            System.err.println("Failed to encode image: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Main method to generate report from command line
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting report generation...");
        generateReport();
        System.out.println("✅ Report generation completed!");
    }
}
