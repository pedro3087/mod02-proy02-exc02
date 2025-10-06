package com.example.navwaits;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for handling multiple browser windows/tabs
 * Demonstrates window switching, handle management, and assertions
 */
public class WindowsAlertsFramesFormTest extends BaseTest {

    @Test
    public void testWindows() {
        // Navigate to the windows page
        driver.get("https://the-internet.herokuapp.com/windows");
        
        // Get and store the original window handle
        String originalHandle = driver.getWindowHandle();
        System.out.println("Original window handle: " + originalHandle);
        
        // Find and click the "Click Here" link
        WebElement clickHereLink = driver.findElement(By.linkText("Click Here"));
        clickHereLink.click();
        
        // Use WebDriverWait to wait until there are 2 window handles
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        
        // Get all window handles
        Set<String> allHandles = driver.getWindowHandles();
        System.out.println("Total window handles: " + allHandles.size());
        
        // Find and switch to the new window handle
        String newWindowHandle = null;
        for (String handle : allHandles) {
            if (!handle.equals(originalHandle)) {
                newWindowHandle = handle;
                break;
            }
        }
        
        // Assert that we found a new window handle
        assert newWindowHandle != null : "New window handle not found";
        
        // Switch to the new window
        driver.switchTo().window(newWindowHandle);
        System.out.println("Switched to new window handle: " + newWindowHandle);
        
        // Assert that the new window's title is "New Window"
        String newWindowTitle = driver.getTitle();
        System.out.println("New window title: " + newWindowTitle);
        assertEquals("New Window", newWindowTitle, "New window title should be 'New Window'");
        
        // Close the new window
        driver.close();
        System.out.println("Closed the new window");
        
        // Switch back to the original window
        driver.switchTo().window(originalHandle);
        System.out.println("Switched back to original window");
        
        // Verify we're back to the original window
        String currentTitle = driver.getTitle();
        System.out.println("Current window title: " + currentTitle);
        assertEquals("The Internet", currentTitle, "Should be back to the original window");
    }
}
