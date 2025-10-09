package com.example.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import com.example.utils.ScreenshotUtil;
import com.example.utils.TestHelper;
import com.example.utils.ExecutionIdManager;
import com.example.utils.ReportGenerator;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

/**
 * Enhanced BaseTest with smart failure handling
 * Automatically handles test failures, captures screenshots, and generates reports
 */
@ExtendWith(SmartTestExecutionListener.class)
public class SmartBaseTest {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    // Screenshot configuration constants
    private static final String SCREENSHOT_MODE_PROPERTY = "screenshot.mode";
    private static final String FAILURE_ONLY_MODE = "failure-only";
    private static final String ALL_STEPS_MODE = "all";
    private static final String DEFAULT_MODE = FAILURE_ONLY_MODE; // Default to failure-only mode
    
    // Screenshot mode for current test
    protected boolean useFailureOnlyScreenshots;

    protected WebDriver createDriver() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                // Configuración para cerrar pop-ups automáticamente
                firefoxOptions.addPreference("dom.disable_beforeunload", true);
                firefoxOptions.addPreference("dom.popup_maximum", 0);
                firefoxOptions.addPreference("dom.disable_open_during_load", false);
                // Configuración específica para evitar pop-ups de gestor de contraseñas
                firefoxOptions.addPreference("signon.rememberSignons", false);
                firefoxOptions.addPreference("signon.autofillForms", false);
                firefoxOptions.addPreference("signon.generation.enabled", false);
                firefoxOptions.addPreference("signon.management.page.breach-alerts.enabled", false);
                firefoxOptions.addPreference("privacy.trackingprotection.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.malware.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.phishing.enabled", false);
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                // Configuración para cerrar pop-ups automáticamente
                edgeOptions.addArguments("--disable-popup-blocking");
                edgeOptions.addArguments("--disable-notifications");
                edgeOptions.addArguments("--disable-extensions");
                // Configuración específica para evitar pop-ups de gestor de contraseñas
                edgeOptions.addArguments("--disable-password-manager");
                edgeOptions.addArguments("--disable-save-password-bubble");
                edgeOptions.addArguments("--disable-autofill-keyboard-accessory-view");
                edgeOptions.addArguments("--disable-features=TranslateUI");
                edgeOptions.addArguments("--disable-client-side-phishing-detection");
                edgeOptions.addArguments("--disable-sync");
                edgeOptions.addArguments("--no-first-run");
                edgeOptions.addArguments("--no-default-browser-check");
                // Deshabilitar completamente el gestor de contraseñas
                edgeOptions.setExperimentalOption("prefs", java.util.Map.of(
                    "credentials_enable_service", false,
                    "password_manager_enabled", false,
                    "profile.password_manager_enabled", false,
                    "profile.default_content_setting_values.notifications", 2
                ));
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                // Initialize WebDriverManager for Chrome
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                // Configuración para cerrar pop-ups automáticamente
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-infobars");
                options.addArguments("--disable-web-security");
                options.addArguments("--remote-allow-origins=*");
                // Configuración específica para evitar pop-ups de Google Password Manager
                options.addArguments("--disable-password-manager");
                options.addArguments("--disable-save-password-bubble");
                options.addArguments("--disable-autofill-keyboard-accessory-view");
                options.addArguments("--disable-features=TranslateUI");
                options.addArguments("--disable-ipc-flooding-protection");
                options.addArguments("--disable-background-timer-throttling");
                options.addArguments("--disable-renderer-backgrounding");
                options.addArguments("--disable-backgrounding-occluded-windows");
                options.addArguments("--disable-client-side-phishing-detection");
                options.addArguments("--disable-sync");
                options.addArguments("--disable-default-apps");
                options.addArguments("--no-first-run");
                options.addArguments("--no-default-browser-check");
                // Deshabilitar completamente el gestor de contraseñas
                options.setExperimentalOption("prefs", java.util.Map.of(
                    "credentials_enable_service", false,
                    "password_manager_enabled", false,
                    "profile.password_manager_enabled", false,
                    "profile.default_content_setting_values.notifications", 2
                ));
                driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        // Maximizar la ventana del navegador
        driver.manage().window().maximize();
        
        return driver;
    }

    @BeforeEach
    public void setUp() {
        if (driver == null) {
            createDriver();
        }
        // Initialize WebDriverWait with 10 seconds timeout
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Clean up old screenshots before starting new test execution
        cleanOldScreenshots();
        
        // Configure screenshot mode based on system property or default
        configureScreenshotMode();
        
        // Cerrar cualquier pop-up que pueda aparecer al iniciar
        closePopups();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Método para cerrar automáticamente cualquier pop-up que aparezca
     */
    protected void closePopups() {
        try {
            // Cerrar alertas de JavaScript
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
        } catch (NoAlertPresentException e) {
            // No hay alertas presentes, continuar normalmente
        }
    }

    /**
     * Método público para cerrar pop-ups desde los tests
     */
    public void closeAnyPopups() {
        closePopups();
    }
    
    /**
     * Configure screenshot mode based on system property or default
     */
    private void configureScreenshotMode() {
        String screenshotMode = System.getProperty(SCREENSHOT_MODE_PROPERTY, DEFAULT_MODE);
        useFailureOnlyScreenshots = FAILURE_ONLY_MODE.equalsIgnoreCase(screenshotMode);
        
        // Apply the configuration
        TestHelper.setupScreenshotMode(useFailureOnlyScreenshots);
        
        System.out.println("📸 Screenshot mode: " + (useFailureOnlyScreenshots ? "FAILURE-ONLY" : "ALL-STEPS"));
    }
    
    /**
     * Override screenshot mode for specific tests
     * @param failureOnly true for failure-only mode, false for all steps
     */
    protected void setScreenshotMode(boolean failureOnly) {
        useFailureOnlyScreenshots = failureOnly;
        TestHelper.setupScreenshotMode(failureOnly);
        System.out.println("📸 Screenshot mode overridden to: " + (failureOnly ? "FAILURE-ONLY" : "ALL-STEPS"));
    }
    
    /**
     * Capture screenshot with current mode settings
     * @param stepName Name of the step
     * @return File path of the screenshot, or null if not captured
     */
    protected String captureScreenshot(String stepName) {
        return TestHelper.captureScreenshot(driver, stepName);
    }
    
    /**
     * Handle test failure with screenshot capture
     * @param stepName Name of the step that failed
     * @param exception The exception that occurred
     * @return File path of the failure screenshot
     */
    protected String handleTestFailure(String stepName, Exception exception) {
        return TestHelper.handleTestFailure(driver, stepName, exception);
    }
    
    /**
     * Handle assertion failure with screenshot capture
     * @param stepName Name of the step that failed
     * @param assertionMessage The assertion message
     * @return File path of the failure screenshot
     */
    protected String handleAssertionFailure(String stepName, String assertionMessage) {
        return TestHelper.handleAssertionFailure(driver, stepName, assertionMessage);
    }
    
    /**
     * Get current screenshot mode
     * @return true if in failure-only mode, false if capturing all screenshots
     */
    protected boolean isFailureOnlyMode() {
        return useFailureOnlyScreenshots;
    }
    
    /**
     * Clean up old screenshots before starting new test execution
     * Only clean up screenshots from previous test runs, not current session
     */
    private void cleanOldScreenshots() {
        try {
            File screenshotDir = new File("target/screenshots");
            if (!screenshotDir.exists()) {
                return;
            }
            
            File[] allScreenshots = screenshotDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (allScreenshots == null || allScreenshots.length == 0) {
                return;
            }
            
            // Group screenshots by execution ID
            Map<String, List<File>> executionGroups = new HashMap<>();
            
            for (File screenshot : allScreenshots) {
                String filename = screenshot.getName();
                String executionId = ExecutionIdManager.extractExecutionIdFromFilename(filename);
                executionGroups.computeIfAbsent(executionId, k -> new ArrayList<>()).add(screenshot);
            }
            
            // Get current execution ID
            String currentExecutionId = ExecutionIdManager.getCurrentExecutionId();
            
            int deletedCount = 0;
            for (Map.Entry<String, List<File>> entry : executionGroups.entrySet()) {
                String executionId = entry.getKey();
                
                // Only keep screenshots from the current execution ID
                // Delete all other screenshots (from previous test runs)
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
                System.out.println("📸 Keeping all screenshots from current test session");
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Failed to clean old screenshots: " + e.getMessage());
        }
    }
    
    /**
     * Execute a test step with automatic failure handling
     * @param stepName Name of the step being executed
     * @param stepDescription Description of what the step does
     * @param stepCode The code to execute
     * @return Result of the step execution
     */
    protected <T> T executeStep(String stepName, String stepDescription, Supplier<T> stepCode) {
        System.out.println("🔄 Executing step: " + stepName + " - " + stepDescription);
        
        try {
            T result = stepCode.get();
            System.out.println("✅ Step completed: " + stepName);
            return result;
        } catch (Exception e) {
            System.err.println("❌ Step failed: " + stepName + " - " + e.getMessage());
            handleStepFailure(stepName, e);
            throw e; // Re-throw to maintain test failure behavior
        }
    }
    
    /**
     * Execute a test step with automatic failure handling (void return)
     * @param stepName Name of the step being executed
     * @param stepDescription Description of what the step does
     * @param stepCode The code to execute
     */
    protected void executeStep(String stepName, String stepDescription, Runnable stepCode) {
        executeStep(stepName, stepDescription, () -> {
            stepCode.run();
            return null;
        });
    }
    
    /**
     * Execute a test step with automatic failure handling and screenshot capture
     * @param stepName Name of the step being executed
     * @param stepDescription Description of what the step does
     * @param stepCode The code to execute
     * @param captureScreenshot Whether to capture screenshot on failure
     * @return Result of the step execution
     */
    protected <T> T executeStep(String stepName, String stepDescription, Supplier<T> stepCode, boolean captureScreenshot) {
        System.out.println("🔄 Executing step: " + stepName + " - " + stepDescription);
        
        try {
            T result = stepCode.get();
            System.out.println("✅ Step completed: " + stepName);
            return result;
        } catch (Exception e) {
            System.err.println("❌ Step failed: " + stepName + " - " + e.getMessage());
            handleStepFailure(stepName, e, captureScreenshot);
            throw e; // Re-throw to maintain test failure behavior
        }
    }
    
    /**
     * Handle step failure with automatic screenshot capture
     * @param stepName Name of the failed step
     * @param exception The exception that occurred
     */
    protected void handleStepFailure(String stepName, Exception exception) {
        // Don't capture screenshot here - let the SmartTestExecutionListener handle it
        // to avoid duplicate screenshots
        handleStepFailure(stepName, exception, false);
    }
    
    /**
     * Handle step failure with optional screenshot capture
     * @param stepName Name of the failed step
     * @param exception The exception that occurred
     * @param captureScreenshot Whether to capture screenshot
     */
    protected void handleStepFailure(String stepName, Exception exception, boolean captureScreenshot) {
        // Record failure in tracker
        String testName = TestResultTracker.getCurrentTestName();
        if (testName != null) {
            TestResultTracker.recordFailure(testName, stepName, exception);
        }
        
        // Capture screenshot if requested and in appropriate mode
        if (captureScreenshot) {
            String screenshotPath = handleTestFailure(stepName, exception);
            if (screenshotPath != null && testName != null) {
                TestResultTracker.recordScreenshot(testName);
            }
        }
    }
    
    /**
     * Safe element interaction with automatic failure handling
     * @param stepName Name of the step
     * @param elementSupplier Supplier that returns the WebElement
     * @param interaction Interaction to perform on the element
     */
    protected void safeElementInteraction(String stepName, Supplier<WebElement> elementSupplier, 
                                        java.util.function.Consumer<WebElement> interaction) {
        executeStep(stepName, "Interacting with element", () -> {
            WebElement element = elementSupplier.get();
            interaction.accept(element);
        });
    }
    
    /**
     * Safe navigation with automatic failure handling
     * @param stepName Name of the step
     * @param url URL to navigate to
     */
    protected void safeNavigate(String stepName, String url) {
        executeStep(stepName, "Navigating to " + url, () -> {
            driver.get(url);
        });
    }
    
    /**
     * Safe wait with automatic failure handling
     * @param stepName Name of the step
     * @param conditionDescription Description of what we're waiting for
     * @param waitCondition The wait condition
     */
    protected <T> T safeWait(String stepName, String conditionDescription, 
                            java.util.function.Function<WebDriverWait, T> waitCondition) {
        return executeStep(stepName, "Waiting for " + conditionDescription, () -> {
            return waitCondition.apply(wait);
        });
    }
    
    /**
     * Safe assertion with automatic failure handling
     * @param stepName Name of the step
     * @param assertionDescription Description of the assertion
     * @param assertion The assertion to perform
     */
    protected void safeAssert(String stepName, String assertionDescription, Runnable assertion) {
        executeStep(stepName, "Asserting " + assertionDescription, () -> {
            assertion.run();
        });
    }
    
    /**
     * Generate test report for current test
     */
    protected void generateTestReport() {
        System.out.println("📊 Generating test report...");
        ReportGenerator.generateReport();
    }
    
    /**
     * Get current test execution info
     */
    protected TestResultTracker.TestExecutionInfo getCurrentTestInfo() {
        String testName = TestResultTracker.getCurrentTestName();
        return testName != null ? TestResultTracker.getTestInfo(testName) : null;
    }
    
    /**
     * Check if current test has failures
     */
    protected boolean hasCurrentTestFailures() {
        return TestResultTracker.hasCurrentTestFailures();
    }
    
    /**
     * Get test execution summary
     */
    protected String getTestExecutionSummary() {
        return TestResultTracker.getExecutionSummary();
    }
}
