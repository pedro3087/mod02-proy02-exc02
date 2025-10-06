package com.example.navwaits;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class InventoryFlowTest extends BaseTest {
    @Test
    @DisplayName("Inventory flow test with navigation, element validation, and advanced waits")
    void testInventoryFlow() {
        // Reset screenshot counter for this test
        ScreenshotUtil.resetStepCounter();
        
        driver.get("https://www.saucedemo.com/");
        ScreenshotUtil.captureScreenshot(driver, "01_Login_Page");

        // CSS Locators
        WebElement username = driver.findElement(By.cssSelector("input#user-name"));
        WebElement password = driver.findElement(By.cssSelector("input#password"));
        WebElement loginBtn = driver.findElement(By.cssSelector("input#login-button"));

        username.sendKeys("standard_user");
        password.sendKeys("secret_sauce");
        ScreenshotUtil.captureScreenshot(driver, "02_Credentials_Entered");
        loginBtn.click();

        // Explicit wait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.and(
            ExpectedConditions.urlContains("inventory"),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".inventory_list"))
        ));
        ScreenshotUtil.captureScreenshot(driver, "03_Inventory_Page_Loaded");

        // Find and click on the first "Add to cart" button using robust locator
        WebElement firstAddToCart = driver.findElement(By.xpath("//button[contains(@class,'btn_inventory') and (normalize-space()='Add to cart' or normalize-space()='Add to Cart')][1]"));
        
        // Debug: Print the button text before clicking
        System.out.println("Button text before click: " + firstAddToCart.getText());
        ScreenshotUtil.captureScreenshot(driver, "04_Before_Add_To_Cart_Click");
        
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
        ScreenshotUtil.captureScreenshot(driver, "05_After_Add_To_Cart_Click");
        
        // Verify the button text changes (check for any text that contains "remove" case-insensitive)
        wait.until(ExpectedConditions.or(
            ExpectedConditions.textToBePresentInElement(updatedButton, "Remove"),
            ExpectedConditions.textToBePresentInElement(updatedButton, "REMOVE"),
            ExpectedConditions.textToBePresentInElement(updatedButton, "remove")
        ));

        // Verify that the cart badge shows "1"
        wait.until(ExpectedConditions.textToBe(By.cssSelector("#shopping_cart_container .shopping_cart_badge"), "1"));
        ScreenshotUtil.captureScreenshot(driver, "06_Cart_Badge_Shows_1");

        // Navigate to the detail page of the first product
        WebElement firstItemTitle = driver.findElement(By.xpath("(//div[contains(@class,'inventory_item_name')])[1]"));
        firstItemTitle.click();

        // Validate that .inventory_details_name appears on the detail page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".inventory_details_name")));
        System.out.println("Product detail page loaded successfully");
        ScreenshotUtil.captureScreenshot(driver, "07_Product_Detail_Page");

        // Go back to the inventory page
        driver.navigate().back();
        
        // Wait for the inventory page to load
        wait.until(ExpectedConditions.and(
            ExpectedConditions.urlContains("inventory"),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".inventory_list"))
        ));
        System.out.println("Returned to inventory page");
        ScreenshotUtil.captureScreenshot(driver, "08_Returned_To_Inventory");

        // Confirm that the cart badge is still "1"
        wait.until(ExpectedConditions.textToBe(By.cssSelector("#shopping_cart_container .shopping_cart_badge"), "1"));
        System.out.println("Cart badge confirmed to still show '1'");
        ScreenshotUtil.captureScreenshot(driver, "09_Final_Cart_Verification");
        
        // Generate beautiful HTML report with embedded screenshots
        System.out.println("📊 Generating beautiful HTML report with screenshots...");
        ReportGenerator.generateReport();
    }
}
