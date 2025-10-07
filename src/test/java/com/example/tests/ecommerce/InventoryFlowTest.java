package com.example.tests.ecommerce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.example.base.BaseTest;
import com.example.utils.ReportGenerator;

public class InventoryFlowTest extends BaseTest {
    @Test
    @DisplayName("Inventory flow test with centralized screenshot configuration")
    void testInventoryFlow() {
        try {
            driver.get("https://www.saucedemo.com/");
            System.out.println("✅ Navigated to login page");

            // CSS Locators
            WebElement username = driver.findElement(By.cssSelector("input#user-name"));
            WebElement password = driver.findElement(By.cssSelector("input#password"));
            WebElement loginBtn = driver.findElement(By.cssSelector("input#login-button"));

            username.sendKeys("standard_user");
            password.sendKeys("secret_sauce");
            System.out.println("✅ Credentials entered");
            loginBtn.click();

            // Explicit wait
            wait.until(ExpectedConditions.and(
                ExpectedConditions.urlContains("inventory"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".inventory_list"))
            ));
            System.out.println("✅ Inventory page loaded");

            // Find and click on the first "Add to cart" button using robust locator
            WebElement firstAddToCart = driver.findElement(By.xpath("//button[contains(@class,'btn_inventory') and (normalize-space()='Add to cart' or normalize-space()='Add to Cart')][1]"));
            
            // Debug: Print the button text before clicking
            System.out.println("Button text before click: " + firstAddToCart.getText());
            
            // Ensure the button is clickable before clicking
            wait.until(ExpectedConditions.elementToBeClickable(firstAddToCart));
            
            // Try JavaScript click instead of regular click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstAddToCart);
            
            // Wait a moment for the button to update
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Re-find the button after click (it becomes stale after DOM update)
            WebElement updatedButton = driver.findElement(By.xpath("//button[contains(@class,'btn_inventory') and (normalize-space()='Remove' or normalize-space()='REMOVE' or normalize-space()='remove')][1]"));
            
            // Debug: Print the button text after clicking
            System.out.println("Button text after click: " + updatedButton.getText());
            
            // Verify the button text changes (check for any text that contains "remove" case-insensitive)
            wait.until(ExpectedConditions.or(
                ExpectedConditions.textToBePresentInElement(updatedButton, "Remove"),
                ExpectedConditions.textToBePresentInElement(updatedButton, "REMOVE"),
                ExpectedConditions.textToBePresentInElement(updatedButton, "remove")
            ));
            System.out.println("✅ Button text changed to 'Remove'");

            // Verify that the cart badge shows "1"
            wait.until(ExpectedConditions.textToBe(By.cssSelector("#shopping_cart_container .shopping_cart_badge"), "1"));
            System.out.println("✅ Cart badge shows '1'");

            // Navigate to the detail page of the first product
            WebElement firstItemTitle = driver.findElement(By.xpath("(//div[contains(@class,'inventory_item_name')])[1]"));
            firstItemTitle.click();

            // Validate that .inventory_details_name appears on the detail page
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".inventory_details_name")));
            System.out.println("✅ Product detail page loaded successfully");

            // Go back to the inventory page
            driver.navigate().back();
            
            // Wait for the inventory page to load
            wait.until(ExpectedConditions.and(
                ExpectedConditions.urlContains("inventory"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".inventory_list"))
            ));
            System.out.println("✅ Returned to inventory page");

            // Confirm that the cart badge is still "1"
            wait.until(ExpectedConditions.textToBe(By.cssSelector("#shopping_cart_container .shopping_cart_badge"), "1"));
            System.out.println("✅ Cart badge confirmed to still show '1'");
            
            System.out.println("🎉 Test completed successfully - Screenshots captured based on configuration");
            
        } catch (Exception e) {
            // Handle any failures and capture screenshots using centralized method
            handleTestFailure("Inventory_Flow_Test", e);
            throw e; // Re-throw to fail the test
        } finally {
            // Generate beautiful HTML report with embedded screenshots
            System.out.println("📊 Generating beautiful HTML report with screenshots...");
            ReportGenerator.generateReport();
        }
    }
}
