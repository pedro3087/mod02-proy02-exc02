package com.example.base;

import org.junit.jupiter.api.extension.*;
import com.example.utils.ReportGenerator;
import com.example.utils.ExecutionIdManager;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Smart Test Execution Listener that automatically handles test failures
 * Provides centralized failure handling, screenshot capture, and reporting
 */
public class SmartTestExecutionListener implements 
    BeforeEachCallback, 
    AfterEachCallback, 
    TestExecutionExceptionHandler,
    BeforeAllCallback,
    AfterAllCallback {
    
    private static final String EXECUTION_START_TIME = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // Get the execution ID (automatically generated when ExecutionIdManager is first loaded)
        String executionId = ExecutionIdManager.getCurrentExecutionId();
        
        System.out.println("🧪 Starting Test Suite: " + context.getDisplayName());
        System.out.println("⏰ Execution started at: " + EXECUTION_START_TIME);
        System.out.println("🆔 Execution ID: " + executionId);
        System.out.println("=" .repeat(60));
    }
    
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        String testName = getTestName(context);
        TestResultTracker.startTest(testName);
        
        // Log test start with additional context
        System.out.println("🔍 Test Method: " + context.getTestMethod().map(Method::getName).orElse("Unknown"));
        System.out.println("📝 Test Class: " + context.getTestClass().map(Class::getSimpleName).orElse("Unknown"));
    }
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        String testName = getTestName(context);
        TestResultTracker.endTest(testName);
        
        // Don't generate report after each test - wait for afterAll
        // This allows all failures from the test class to be accumulated
    }
    
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        System.out.println("=" .repeat(60));
        System.out.println("🏁 Test Suite Completed: " + context.getDisplayName());
        System.out.println(TestResultTracker.getExecutionSummary());
        
        // Generate final comprehensive report
        System.out.println("📊 Generating final comprehensive report...");
        ReportGenerator.generateReport();
        
        // Note: Execution ID is not cleared here to allow multiple test classes
        // in the same Maven run to share the same execution ID
    }
    
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        String testName = getTestName(context);
        String testClass = context.getTestClass().map(Class::getSimpleName).orElse("Unknown");
        String testMethod = context.getTestMethod().map(method -> method.getName()).orElse("Unknown");
        String exceptionType = throwable.getClass().getSimpleName();
        
        // Create a more descriptive step name with test context
        String stepName = String.format("%s_%s_%s_Failure", testClass, testMethod, exceptionType);
        
        // Record the failure with detailed context
        TestResultTracker.recordFailure(testName, stepName, 
            throwable instanceof Exception ? (Exception) throwable : new RuntimeException(throwable));
        
        // Capture failure screenshot if we have access to WebDriver
        captureFailureScreenshot(context, stepName, throwable);
        
        // Log the failure details with more context
        System.out.println("💥 Test execution failed: " + testName);
        System.out.println("🔍 Test Class: " + testClass);
        System.out.println("🔍 Test Method: " + testMethod);
        System.out.println("🔍 Exception Type: " + exceptionType);
        System.out.println("🔍 Failure step: " + stepName);
        System.out.println("❌ Exception: " + throwable.getMessage());
        
        // Re-throw the exception to maintain normal test failure behavior
        throw throwable;
    }
    
    /**
     * Extract test name from context
     */
    private String getTestName(ExtensionContext context) {
        return context.getDisplayName();
    }
    
    
    /**
     * Capture failure screenshot if WebDriver is available
     */
    private void captureFailureScreenshot(ExtensionContext context, String stepName, Throwable throwable) {
        try {
            // Try to get WebDriver from the test instance
            Object testInstance = context.getTestInstance().orElse(null);
            if (testInstance instanceof SmartBaseTest) {
                SmartBaseTest baseTest = (SmartBaseTest) testInstance;
                String screenshotPath = baseTest.handleTestFailure(stepName, 
                    throwable instanceof Exception ? (Exception) throwable : new RuntimeException(throwable));
                
                if (screenshotPath != null) {
                    TestResultTracker.recordScreenshot(getTestName(context));
                    System.out.println("📸 Failure screenshot captured: " + screenshotPath);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not capture failure screenshot: " + e.getMessage());
        }
    }
}
