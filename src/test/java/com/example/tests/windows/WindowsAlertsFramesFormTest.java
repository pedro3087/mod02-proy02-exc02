package com.example.tests.windows;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.base.BaseTest;

/**
 * Test class for handling multiple browser windows/tabs and JavaScript alerts
 * Demonstrates window switching, handle management, alert handling, and assertions
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

    @Test
    public void testAlerts() {
        // Navigate to the JavaScript alerts page
        driver.get("https://the-internet.herokuapp.com/javascript_alerts");
        
        // Test 1: JS Alert
        System.out.println("Testing JS Alert...");
        WebElement jsAlertButton = driver.findElement(By.xpath("//button[text()='Click for JS Alert']"));
        jsAlertButton.click();
        
        // Wait for the alert and accept it
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        
        // Validate the result text
        WebElement result = driver.findElement(By.id("result"));
        String alertResult = result.getText();
        System.out.println("JS Alert result: " + alertResult);
        assertEquals("You successfully clicked an alert", alertResult, "JS Alert result should match expected text");
        
        // Test 2: JS Confirm - Dismiss
        System.out.println("Testing JS Confirm (dismiss)...");
        WebElement jsConfirmButton = driver.findElement(By.xpath("//button[text()='Click for JS Confirm']"));
        jsConfirmButton.click();
        
        // Wait for the alert and dismiss it
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
        
        // Validate the result text
        result = driver.findElement(By.id("result"));
        String confirmResult = result.getText();
        System.out.println("JS Confirm result: " + confirmResult);
        assertEquals("You clicked: Cancel", confirmResult, "JS Confirm dismiss result should match expected text");
        
        // Test 3: JS Prompt - Send keys and accept
        System.out.println("Testing JS Prompt (send keys and accept)...");
        WebElement jsPromptButton = driver.findElement(By.xpath("//button[text()='Click for JS Prompt']"));
        jsPromptButton.click();
        
        // Wait for the alert, send keys, and accept it
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().sendKeys("Hola QA");
        driver.switchTo().alert().accept();
        
        // Validate the result text
        result = driver.findElement(By.id("result"));
        String promptResult = result.getText();
        System.out.println("JS Prompt result: " + promptResult);
        assertEquals("You entered: Hola QA", promptResult, "JS Prompt result should match expected text");
        
        System.out.println("All alert tests completed successfully!");
    }

    @Test
    public void testFrames() {
        // Navigate to the iframe page
        driver.get("https://the-internet.herokuapp.com/iframe");
        
        // Wait for iframe to be available and switch to it
        System.out.println("Switching to iframe...");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("mce_0_ifr")));
        
        // Find the text editor element inside the iframe
        WebElement textEditor = driver.findElement(By.id("tinymce"));
        System.out.println("Found text editor inside iframe");
        
        // Use JavaScript to clear and set content in TinyMCE editor
        String testText = "Text inside the frame";
        ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = '';", textEditor);
        ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", textEditor, testText);
        System.out.println("Set text in editor using JavaScript: " + testText);
        
        // Switch back to the main document
        driver.switchTo().defaultContent();
        System.out.println("Switched back to main document");
        
        // Validation: Re-switch to the iframe to verify content
        System.out.println("Re-switching to iframe for validation...");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("mce_0_ifr")));
        
        // Get the text from the editor
        WebElement editorForValidation = driver.findElement(By.id("tinymce"));
        String actualText = editorForValidation.getText();
        System.out.println("Actual text in editor: " + actualText);
        
        // Assert the text equals what we sent
        assertEquals(testText, actualText, "Text in iframe editor should match the sent text");
        
        // Switch back to default content
        driver.switchTo().defaultContent();
        System.out.println("Switched back to main document after validation");
        
        System.out.println("Iframe test completed successfully!");
    }

    /**
     * Test for form automation
     * Demonstrates filling form fields, handling radio buttons, checkboxes, dropdowns, and form submission
     */
    @Test
    public void testFormAutomation() {
        System.out.println("Starting form automation test...");
        
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        System.out.println("Navigated to form page");

        // Fill the text input field
        WebElement textInput = driver.findElement(By.name("my-text"));
        textInput.clear();
        textInput.sendKeys("Memo QA");
        System.out.println("Filled text input field with: Memo QA");

        // Fill the password field
        WebElement passwordInput = driver.findElement(By.name("my-password"));
        passwordInput.clear();
        passwordInput.sendKeys("Secreto123");
        System.out.println("Filled password field");

        // Fill the textarea field
        WebElement textarea = driver.findElement(By.name("my-textarea"));
        textarea.clear();
        textarea.sendKeys("Some comments for the form.");
        System.out.println("Filled textarea field");

        // Click the radio button labeled "Option 2"
        WebElement radioOption2 = driver.findElement(By.id("my-radio-2"));
        radioOption2.click();
        System.out.println("Selected radio button Option 2");

        // Click the checkbox with fallback for ElementClickInterceptedException
        WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox']"));
        try {
            checkbox.click();
            System.out.println("Clicked checkbox using regular click");
        } catch (Exception e) {
            // Fallback to JavaScript click if regular click fails
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            System.out.println("Clicked checkbox using JavaScript click (fallback)");
        }

        // Select "Two" from the dropdown
        WebElement dropdown = driver.findElement(By.name("my-select"));
        dropdown.click();
        WebElement optionTwo = driver.findElement(By.xpath("//option[normalize-space()='Two']"));
        optionTwo.click();
        System.out.println("Selected 'Two' from dropdown");

        // Click the Submit button
        WebElement submitButton = driver.findElement(By.xpath("//button[normalize-space()='Submit']"));
        submitButton.click();
        System.out.println("Clicked Submit button");

        // Wait for the confirmation message to be visible
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        System.out.println("Confirmation message appeared: " + confirmationMessage.getText());

        // Assert that the confirmation text is "Received!"
        assertEquals("Received!", confirmationMessage.getText(), "Form submission confirmation should be 'Received!'");
        
        System.out.println("Form automation test completed successfully!");
    }
}
