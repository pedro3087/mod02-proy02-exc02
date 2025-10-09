package com.example.tests.windows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.base.SmartBaseTest;

/**
 * Smart Test class demonstrating centralized failure handling
 * Uses SmartBaseTest for automatic failure handling, screenshot capture, and reporting
 */
public class SmartWindowsAlertsFramesFormTest extends SmartBaseTest {

    @Test
    @DisplayName("Window Handling Test")
    public void testWindows() {
        // Navigate to the windows page
        safeNavigate("Navigate_To_Windows_Page", "https://the-internet.herokuapp.com/windows");
        
        // Get and store the original window handle
        String originalHandle = executeStep("Get_Original_Window_Handle", 
            "Getting original window handle", () -> {
                String handle = driver.getWindowHandle();
                System.out.println("Original window handle: " + handle);
                return handle;
            });
        
        // Find and click the "Click Here" link
        safeElementInteraction("Click_New_Window_Link", 
            () -> driver.findElement(By.linkText("Click Here")),
            WebElement::click);
        
        // Wait for new window to open
        safeWait("Wait_For_New_Window", "new window to open", 
            wait -> wait.until(ExpectedConditions.numberOfWindowsToBe(2)));
        
        // Get all window handles and find the new one
        String newWindowHandle = executeStep("Find_New_Window_Handle", 
            "Finding new window handle", () -> {
                Set<String> allHandles = driver.getWindowHandles();
                System.out.println("Total window handles: " + allHandles.size());
                
                String newHandle = null;
                for (String handle : allHandles) {
                    if (!handle.equals(originalHandle)) {
                        newHandle = handle;
                        break;
                    }
                }
                
                assert newHandle != null : "New window handle not found";
                return newHandle;
            });
        
        // Switch to the new window
        executeStep("Switch_To_New_Window", "Switching to new window", () -> {
            driver.switchTo().window(newWindowHandle);
            System.out.println("Switched to new window handle: " + newWindowHandle);
        });
        
        // Verify new window title
        safeAssert("Verify_New_Window_Title", "new window title is 'New Window'", () -> {
            String newWindowTitle = driver.getTitle();
            System.out.println("New window title: " + newWindowTitle);
            assertEquals("New Window", newWindowTitle, "New window title should be 'New Window'");
        });
        
        // Close the new window
        executeStep("Close_New_Window", "Closing new window", () -> {
            driver.close();
            System.out.println("Closed the new window");
        });
        
        // Switch back to the original window
        executeStep("Switch_Back_To_Original", "Switching back to original window", () -> {
            driver.switchTo().window(originalHandle);
            System.out.println("Switched back to original window");
        });
        
        // Verify we're back to the original window
        safeAssert("Verify_Original_Window", "we're back to original window", () -> {
            String currentTitle = driver.getTitle();
            System.out.println("Current window title: " + currentTitle);
            assertEquals("The Internet", currentTitle, "Should be back to the original window");
        });
    }

    @Test
    @DisplayName("JavaScript Alerts Test")
    public void testAlerts() {
        // Navigate to the JavaScript alerts page
        safeNavigate("Navigate_To_Alerts_Page", "https://the-internet.herokuapp.com/javascript_alerts");
        
        // Test 1: JS Alert
        executeStep("Test_JS_Alert", "Testing JS Alert", () -> {
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
        });
        
        // Test 2: JS Confirm - Dismiss
        executeStep("Test_JS_Confirm_Dismiss", "Testing JS Confirm (dismiss)", () -> {
            System.out.println("Testing JS Confirm (dismiss)...");
            WebElement jsConfirmButton = driver.findElement(By.xpath("//button[text()='Click for JS Confirm']"));
            jsConfirmButton.click();
            
            // Wait for the alert and dismiss it
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().dismiss();
            
            // Validate the result text
            WebElement result = driver.findElement(By.id("result"));
            String confirmResult = result.getText();
            System.out.println("JS Confirm result: " + confirmResult);
            assertEquals("You clicked: Cancel", confirmResult, "JS Confirm dismiss result should match expected text");
        });
        
        // Test 3: JS Prompt - Send keys and accept
        executeStep("Test_JS_Prompt", "Testing JS Prompt (send keys and accept)", () -> {
            System.out.println("Testing JS Prompt (send keys and accept)...");
            WebElement jsPromptButton = driver.findElement(By.xpath("//button[text()='Click for JS Prompt']"));
            jsPromptButton.click();
            
            // Wait for the alert, send keys, and accept it
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().sendKeys("Hola QA");
            driver.switchTo().alert().accept();
            
            // Validate the result text
            WebElement result = driver.findElement(By.id("result"));
            String promptResult = result.getText();
            System.out.println("JS Prompt result: " + promptResult);
            assertEquals("You entered: Hola QA", promptResult, "JS Prompt result should match expected text");
        });
        
        System.out.println("All alert tests completed successfully!");
    }

    @Test
    @DisplayName("iFrame Handling Test")
    public void testFrames() {
        // Navigate to the iframe page
        safeNavigate("Navigate_To_Iframe_Page", "https://the-internet.herokuapp.com/iframe");
        
        // Wait for iframe to be available and switch to it
        safeWait("Switch_To_Iframe", "iframe to be available", 
            wait -> wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("mce_0_ifr"))));
        
        System.out.println("Switching to iframe...");
        
        // Find the text editor element inside the iframe
        WebElement textEditor = executeStep("Find_Text_Editor", 
            "Finding text editor inside iframe", () -> {
                WebElement editor = driver.findElement(By.id("tinymce"));
                System.out.println("Found text editor inside iframe");
                return editor;
            });
        
        // Use JavaScript to clear and set content in TinyMCE editor
        executeStep("Set_Text_In_Editor", "Setting text in editor using JavaScript", () -> {
            String testText = "Text inside the frame";
            ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = '';", textEditor);
            ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", textEditor, testText);
            System.out.println("Set text in editor using JavaScript: " + testText);
        });
        
        // Switch back to the main document
        executeStep("Switch_Back_To_Main", "Switching back to main document", () -> {
            driver.switchTo().defaultContent();
            System.out.println("Switched back to main document");
        });
        
        // Validation: Re-switch to the iframe to verify content
        safeWait("Re_Switch_To_Iframe_For_Validation", "iframe for validation", 
            wait -> wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("mce_0_ifr"))));
        
        // Get the text from the editor and validate
        safeAssert("Validate_Text_In_Editor", "text in iframe editor matches expected", () -> {
            System.out.println("Re-switching to iframe for validation...");
            WebElement editorForValidation = driver.findElement(By.id("tinymce"));
            String actualText = editorForValidation.getText();
            System.out.println("Actual text in editor: " + actualText);
            assertEquals("Text inside the frame", actualText, "Text in iframe editor should match the sent text");
        });
        
        // Switch back to default content
        executeStep("Final_Switch_To_Default", "Switching back to main document after validation", () -> {
            driver.switchTo().defaultContent();
            System.out.println("Switched back to main document after validation");
        });
        
        System.out.println("Iframe test completed successfully!");
    }

    @Test
    @DisplayName("Form Automation Test")
    public void testFormAutomation() {
        System.out.println("Starting form automation test...");
        
        // Navigate to form page
        safeNavigate("Navigate_To_Form_Page", "https://www.selenium.dev/selenium/web/web-form.html");
        
        // Fill the text input field
        safeElementInteraction("Fill_Text_Input", 
            () -> driver.findElement(By.name("my-text")),
            element -> {
                element.clear();
                element.sendKeys("Memo QA");
                System.out.println("Filled text input field with: Memo QA");
            });
        
        // Fill the password field
        safeElementInteraction("Fill_Password_Field", 
            () -> driver.findElement(By.name("my-password")),
            element -> {
                element.clear();
                element.sendKeys("Secreto123");
                System.out.println("Filled password field");
            });
        
        // Fill the textarea field
        safeElementInteraction("Fill_Textarea_Field", 
            () -> driver.findElement(By.name("my-textarea")),
            element -> {
                element.clear();
                element.sendKeys("Some comments for the form.");
                System.out.println("Filled textarea field");
            });
        
        // Click the radio button labeled "Option 2"
        safeElementInteraction("Select_Radio_Option_2", 
            () -> driver.findElement(By.id("my-radio-2")),
            element -> {
                element.click();
                System.out.println("Selected radio button Option 2");
            });
        
        // Click the checkbox with fallback for ElementClickInterceptedException
        executeStep("Click_Checkbox", "Clicking checkbox with fallback", () -> {
            WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox']"));
            try {
                checkbox.click();
                System.out.println("Clicked checkbox using regular click");
            } catch (Exception e) {
                // Fallback to JavaScript click if regular click fails
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                System.out.println("Clicked checkbox using JavaScript click (fallback)");
            }
        });
        
        // Select "Two" from the dropdown
        executeStep("Select_Dropdown_Option", "Selecting 'Two' from dropdown", () -> {
            WebElement dropdown = driver.findElement(By.name("my-select"));
            dropdown.click();
            WebElement optionTwo = driver.findElement(By.xpath("//option[normalize-space()='Two']"));
            optionTwo.click();
            System.out.println("Selected 'Two' from dropdown");
        });
        
        // Click the Submit button
        safeElementInteraction("Click_Submit_Button", 
            () -> driver.findElement(By.xpath("//button[normalize-space()='Submit']")),
            element -> {
                element.click();
                System.out.println("Clicked Submit button");
            });
        
        // Wait for the confirmation message to be visible
        WebElement confirmationMessage = safeWait("Wait_For_Confirmation", 
            "confirmation message to be visible", 
            wait -> wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message"))));
        
        System.out.println("Confirmation message appeared: " + confirmationMessage.getText());
        
        // Assert that the confirmation text is "Received!"
        safeAssert("Verify_Confirmation_Message", "confirmation text is 'Received!'", () -> {
            assertEquals("Received!", confirmationMessage.getText(), "Form submission confirmation should be 'Received!'");
        });
        
        System.out.println("Form automation test completed successfully!");
    }

    @Test
    @DisplayName("Failure-Only Screenshot Demo")
    public void testFailureOnlyScreenshots() {
        System.out.println("Starting failure-only screenshot demonstration test...");
        
        // Navigate to form page
        safeNavigate("Navigate_To_Form_Page", "https://www.selenium.dev/selenium/web/web-form.html");
        
        // This screenshot should NOT be captured (no failure yet)
        executeStep("Capture_Screenshot_1", "Capturing screenshot 1 (should NOT be captured)", () -> {
            captureScreenshot("01_Page_Loaded");
            System.out.println("Screenshot 1 - Should NOT be captured (no failure yet)");
        });
        
        // Fill some form fields
        safeElementInteraction("Fill_Form_Fields", 
            () -> driver.findElement(By.name("my-text")),
            element -> {
                element.clear();
                element.sendKeys("Test Input");
                System.out.println("Filled text input field");
            });
        
        // This screenshot should NOT be captured (no failure yet)
        executeStep("Capture_Screenshot_2", "Capturing screenshot 2 (should NOT be captured)", () -> {
            captureScreenshot("02_Form_Filled");
            System.out.println("Screenshot 2 - Should NOT be captured (no failure yet)");
        });
        
        // Intentionally cause a failure by looking for non-existent element
        executeStep("Intentionally_Cause_Failure", 
            "Intentionally causing failure by looking for non-existent element", () -> {
                WebElement nonExistentElement = driver.findElement(By.id("this-element-does-not-exist"));
                nonExistentElement.click();
            });
        
        System.out.println("Failure-only screenshot demonstration completed!");
    }
    
    @Test
    @DisplayName("Test Timeout Failure Scenario")
    public void testTimeoutFailure() {
        System.out.println("Starting timeout failure demonstration test...");
        
        // Navigate to a page that loads slowly
        executeStep("Navigate_To_Slow_Page", 
            "Navigating to a page that might timeout", () -> {
                driver.get("https://httpbin.org/delay/2");
            });
        
        // Try to find an element with a very short timeout (this should fail)
        executeStep("Wait_For_Element_With_Short_Timeout", 
            "Waiting for element with very short timeout (should fail)", () -> {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofMillis(500));
                shortWait.until(ExpectedConditions.presenceOfElementLocated(By.id("this-will-timeout")));
            });
        
        System.out.println("Timeout failure demonstration completed!");
    }
}
