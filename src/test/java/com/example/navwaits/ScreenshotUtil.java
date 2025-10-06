package com.example.navwaits;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static int stepCounter = 1;
    
    public static String captureScreenshot(WebDriver driver, String stepName) {
        try {
            // Create screenshots directory if it doesn't exist
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
            }
            
            // Generate filename with timestamp and step name
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = String.format("step_%02d_%s_%s.png", stepCounter++, stepName.replaceAll("[^a-zA-Z0-9]", "_"), timestamp);
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
    
    public static void resetStepCounter() {
        stepCounter = 1;
    }
}
