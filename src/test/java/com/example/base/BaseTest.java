package com.example.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
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

import java.time.Duration;

public class BaseTest {
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
}
