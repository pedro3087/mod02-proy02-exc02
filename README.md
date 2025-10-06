# Selenium WebDriver Project - Navigation and Waits

A Maven-based Selenium WebDriver project demonstrating web navigation, element interactions, and various wait strategies using JUnit 5. This project showcases robust test automation practices including advanced locator strategies, wait implementations, JavaScript interactions, and beautiful HTML reporting with embedded screenshots.

## 📋 Project Overview

This project contains automated tests for web application testing using Selenium WebDriver, focusing on:
- Web navigation and page interactions
- Advanced element location strategies (CSS selectors, XPath with robust patterns)
- Wait strategies (Explicit waits with combined conditions, FluentWait)
- JavaScript interactions for reliable element clicking
- Cross-browser testing capabilities
- Stale element handling and DOM update management
- Beautiful HTML reporting with embedded screenshots
- Step-by-step visual test documentation
- **WebDriverManager integration for automatic driver management**
- **WebDriverWait support with configurable timeouts**
- **Multiple browser windows/tabs handling**
- **Window switching and management strategies**

## 🛠️ Technology Stack

- **Java**: 11+ (OpenJDK)
- **Maven**: 3.9.11
- **Selenium WebDriver**: 4.23.0
- **JUnit**: 5.10.2 (Jupiter API & Engine)
- **WebDriverManager**: 5.6.3 (Automatic driver management)
- **Test Framework**: Maven Surefire Plugin 3.2.5
- **Reporting**: Maven Surefire Report Plugin 3.2.5
- **Screenshots**: Custom HTML report generator with embedded images

## 📁 Project Structure

```
Mod2-Proy02-Ejercicio2/
├── pom.xml                                    # Maven configuration
├── README.md                                  # Project documentation
└── src/
    ├── main/
    │   └── java/                              # Main source code (empty)
    └── test/
        └── java/
            └── com/
                └── example/
                    ├── navwaits/
                    │   ├── BaseTest.java                    # Base test class with WebDriverManager & WebDriverWait
                    │   ├── InventoryFlowTest.java           # Main test class
                    │   ├── WindowsAlertsFramesFormTest.java # Windows handling test class
                    │   ├── ScreenshotUtil.java              # Screenshot capture utility
                    │   └── ReportGenerator.java             # HTML report generator
                    └── webtesting/                          # Additional test packages
```

## 🚀 Getting Started

### Prerequisites

- **Java 11** or higher
- **Maven 3.9.11** or higher
- **Chrome Browser** (for ChromeDriver tests)
- **Internet connection** (for downloading dependencies and WebDriverManager)

### Installation

1. **Clone or download** this project to your local machine
2. **Navigate** to the project directory:
   ```bash
   cd Mod2-Proy02-Ejercicio2
   ```

3. **Verify Java and Maven** are installed:
   ```bash
   java -version
   mvn -version
   ```

### Running Tests

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
# Run inventory flow test
mvn test -Dtest=InventoryFlowTest

# Run windows handling test
mvn test -Dtest=WindowsAlertsFramesFormTest

# Run all tests
mvn test
```

#### Run Tests with Different Browsers
```bash
# Chrome (default) - Fast execution
mvn test -Dbrowser=chrome

# Firefox - Reliable cross-browser testing
mvn test -Dbrowser=firefox

# Edge - Microsoft Edge browser
mvn test -Dbrowser=edge

# Run specific test class on different browsers
mvn test -Dtest=InventoryFlowTest -Dbrowser=firefox
mvn test -Dtest=InventoryFlowTest -Dbrowser=chrome
mvn test -Dtest=InventoryFlowTest -Dbrowser=edge
```

#### Run Tests in Headless Mode
```bash
# Run all tests in headless mode
mvn test -Dheadless=true

# Run specific test class in headless mode
mvn test -Dtest=InventoryFlowTest -Dheadless=true

# Combine browser and headless options
mvn test -Dtest=InventoryFlowTest -Dbrowser=firefox -Dheadless=true
```

#### Cross-Browser Compatibility
The `InventoryFlowTest` has been validated on multiple browsers:

| Browser | Status | Performance | Notes |
|---------|--------|-------------|-------|
| **Chrome** | ✅ Working | ~7.5s | Fast execution, recommended for development |
| **Firefox** | ✅ Working | ~46s | Reliable, good for cross-browser validation |
| **Edge** | ✅ Supported | ~10-15s | Microsoft Edge compatibility |

**Recommended Usage:**
- **Development**: Use Chrome for faster feedback
- **CI/CD**: Use Firefox for reliable cross-browser testing
- **Production**: Test on all supported browsers

## 📊 Test Reports

### Generate Beautiful HTML Report with Screenshots
```bash
# Run test and generate custom HTML report with embedded screenshots
mvn test -Dtest=InventoryFlowTest

# Generate Maven Surefire report
mvn surefire-report:report
```

### Custom HTML Report with Screenshots
The test automatically generates a beautiful HTML report with embedded screenshots at: `target/reports/test-report-with-screenshots.html`

**Features:**
- 🎨 **Modern Design**: Professional gradient background with responsive layout
- 📸 **Embedded Screenshots**: All 9 test steps captured and embedded (Base64 encoded)
- 📊 **Test Summary**: Statistics, execution time, and status information
- 🔍 **Step Details**: Detailed descriptions for each test step
- 📱 **Responsive**: Works on desktop and mobile devices
- 🚀 **Self-contained**: No external file dependencies

### Open Reports in Browser
```bash
# Windows - Custom report with screenshots
start target/reports/test-report-with-screenshots.html

# Windows - Maven Surefire report
start target/site/surefire-report.html

# macOS
open target/reports/test-report-with-screenshots.html
open target/site/surefire-report.html

# Linux
xdg-open target/reports/test-report-with-screenshots.html
xdg-open target/site/surefire-report.html
```

### Report Locations
- **Custom HTML Report**: `target/reports/test-report-with-screenshots.html` ⭐ **RECOMMENDED**
- **Maven Surefire Report**: `target/site/surefire-report.html`
- **Screenshots Directory**: `target/screenshots/`
- **Text Report**: `target/surefire-reports/com.example.navwaits.InventoryFlowTest.txt`
- **XML Report**: `target/surefire-reports/TEST-com.example.navwaits.InventoryFlowTest.xml`

## 🧪 Test Details

### Test Class: `InventoryFlowTest`

**Test Method**: `testInventoryFlow()`

**Test Steps**:
1. Navigate to `https://www.saucedemo.com/` 📸
2. Login with credentials (`standard_user` / `secret_sauce`) 📸
3. **Combined Wait**: Wait for URL to contain "inventory" AND inventory list to be visible 📸
4. Click first "Add to Cart" button using robust XPath locator 📸
5. **JavaScript Click**: Use JavaScript execution for reliable button clicking 📸
6. **Button Text Verification**: Verify button text changes to "Remove" 📸
7. **Cart Badge Verification**: Verify shopping cart badge shows "1" 📸
8. Navigate to first product detail page 📸
9. Validate product details are visible 📸
10. Navigate back to inventory page 📸
11. **Final Verification**: Confirm cart badge still shows "1" 📸
12. **Generate Report**: Create beautiful HTML report with embedded screenshots

*📸 = Screenshot captured at this step*

**Advanced Locator Strategies Used**:
- **CSS Selectors**: `input#user-name`, `input#password`, `#shopping_cart_container .shopping_cart_badge`
- **Robust XPath**: `//button[contains(@class,'btn_inventory') and (normalize-space()='Add to cart' or normalize-space()='Add to Cart')][1]`
- **Dynamic XPath**: `(//div[contains(@class,'inventory_item_name')])[1]` for product titles

**Wait Strategies Demonstrated**:
- **Combined Explicit Wait**: `ExpectedConditions.and()` for multiple conditions
- **URL and Element Wait**: `urlContains("inventory")` + `visibilityOfElementLocated()`
- **Text Verification Wait**: `textToBePresentInElement()` with case variations
- **Element Clickability Wait**: `elementToBeClickable()` before interaction
- **Stale Element Handling**: Re-finding elements after DOM updates
- **Navigation Wait**: Waiting for page transitions and element visibility

### Test Class: `WindowsAlertsFramesFormTest`

**Test Method**: `testWindows()`

**Test Steps**:
1. Navigate to `https://the-internet.herokuapp.com/windows`
2. **Window Handle Management**: Get and store the original window handle
3. **Link Interaction**: Find and click the "Click Here" link
4. **Window Wait**: Use WebDriverWait to wait until 2 windows are present
5. **Window Switching**: Iterate through handles to find the new window
6. **Title Assertion**: Verify the new window title is "New Window"
7. **Window Cleanup**: Close the new window
8. **Return to Original**: Switch back to the original window
9. **Verification**: Confirm we're back to the original window

**Window Handling Strategies Demonstrated**:
- **Window Handle Management**: Storing and tracking original window handle
- **WebDriverWait for Windows**: `numberOfWindowsToBe(2)` condition
- **Window Iteration**: Finding new window by comparing handles
- **Window Switching**: `driver.switchTo().window(handle)` for navigation
- **Window Cleanup**: Proper resource management with `driver.close()`
- **Assertion Testing**: JUnit 5 assertions for window title verification

**Key Features**:
- **Robust Window Detection**: Handles multiple windows safely
- **Proper Resource Management**: Ensures windows are closed properly
- **Assertion Validation**: Verifies expected window behavior
- **Console Logging**: Detailed output for debugging and verification

### Base Test Class: `BaseTest`

**Features**:
- **WebDriverManager Integration**: Automatic ChromeDriver management
- **WebDriverWait Support**: 10-second timeout for explicit waits
- Multi-browser support (Chrome, Firefox, Edge)
- Headless mode capability
- Configurable browser selection via system properties
- Automatic driver management (setup/teardown)
- Window size configuration (1280x900)
- Protected WebDriver and WebDriverWait instances for test classes

## ⚙️ Configuration

### Maven Configuration (`pom.xml`)

**Key Properties**:
- Java Compiler Release: 11+
- Selenium Version: 4.23.0
- JUnit Version: 5.10.2
- WebDriverManager Version: 5.6.3
- Surefire Plugin: 3.2.5

**Dependencies**:
- `selenium-java`: Complete Selenium WebDriver library
- `junit-jupiter-api`: JUnit 5 API for test annotations
- `junit-jupiter-engine`: JUnit 5 test engine for execution
- `webdrivermanager`: Automatic WebDriver management
- `extentreports`: Rich HTML reporting capabilities
- `aspectjweaver`: Advanced reporting and screenshot integration

### System Properties

| Property | Default | Description |
|----------|---------|-------------|
| `browser` | `chrome` | Browser to use for testing |
| `headless` | `false` | Run browser in headless mode |

## 🔧 Troubleshooting

### Common Issues

1. **Java Not Found**
   - Ensure Java 21+ is installed and in PATH
   - Set `JAVA_HOME` environment variable

2. **Maven Not Found**
   - Ensure Maven 3.9.11+ is installed and in PATH
   - Verify `MAVEN_HOME` is set correctly

3. **ChromeDriver Issues**
   - WebDriverManager automatically handles ChromeDriver setup
   - Ensure Chrome browser is installed
   - WebDriverManager will download the appropriate driver version

4. **Test Failures**
   - Check internet connection for web application access
   - Verify element selectors are still valid
   - Review test reports for detailed error information

5. **JavaScript Click Issues**
   - Ensure JavaScript is enabled in the browser
   - Check for JavaScript errors in browser console
   - Verify element is not covered by other elements

6. **Stale Element Reference**
   - Re-find elements after DOM updates
   - Use explicit waits before interacting with elements
   - Avoid storing element references across page changes

### Environment Setup

**Windows**:
```bash
# Add to system PATH
C:\Program Files\Java\java-11\bin
C:\Program Files\Java\maven\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin

# Set JAVA_HOME
JAVA_HOME=C:\Program Files\Java\java-11
```

## 📈 Project Status

- ✅ Maven build system configured
- ✅ Selenium WebDriver integration complete
- ✅ JUnit 5 test framework setup (API & Engine)
- ✅ WebDriverManager integration for automatic driver management
- ✅ WebDriverWait support with 10-second timeout
- ✅ Multi-browser support implemented
- ✅ HTML reporting configured
- ✅ **Robust locator strategies implemented**
- ✅ **Advanced wait strategies with combined conditions**
- ✅ **JavaScript click implementation for reliable interactions**
- ✅ **Stale element handling and DOM update management**
- ✅ **Button text verification and cart badge validation**
- ✅ **Screenshot capture utility implemented**
- ✅ **Beautiful HTML report generator with embedded screenshots**
- ✅ **Step-by-step visual test documentation**
- ✅ **Multiple browser windows/tabs handling implemented**
- ✅ **Window switching and management strategies**
- ✅ **All tests passing successfully**

## 🤝 Contributing

1. Fork the project
2. Create a feature branch
3. Make your changes
4. Run tests to ensure they pass
5. Submit a pull request

## 📝 License

This project is for educational purposes and demonstrates Selenium WebDriver testing concepts.

## 📞 Support

For questions or issues:
1. Check the troubleshooting section above
2. Review test reports for detailed error information
3. Ensure all prerequisites are properly installed

---

## 🎯 Key Features Implemented

### Advanced Wait Strategies
- **Combined Conditions**: Using `ExpectedConditions.and()` to wait for multiple conditions simultaneously
- **URL and Element Verification**: Waiting for both URL to contain "inventory" and element to be visible
- **Text Change Detection**: Verifying button text changes with case-insensitive matching

### Robust Element Interaction
- **JavaScript Click**: Using `JavascriptExecutor` for reliable button clicking when regular clicks fail
- **Stale Element Handling**: Re-finding elements after DOM updates to avoid StaleElementReferenceException
- **Element Clickability**: Ensuring elements are clickable before interaction

### Advanced Locator Strategies
- **Robust XPath**: Using `contains()` and `normalize-space()` for flexible text matching
- **Dynamic Selectors**: Adapting to different text variations (Add to cart vs Add to Cart)
- **CSS Selector Precision**: Using specific selectors like `#shopping_cart_container .shopping_cart_badge`

### Test Validation
- **Button State Verification**: Confirming button text changes from "Add to cart" to "Remove"
- **Cart Badge Validation**: Verifying shopping cart badge updates to show "1"
- **Page Navigation**: Successfully navigating to product detail pages
- **Cart Persistence**: Verifying cart state remains consistent across page navigation

### Visual Documentation
- **Screenshot Capture**: Automatic screenshot capture at each test step
- **HTML Report Generation**: Beautiful, self-contained HTML report with embedded screenshots
- **Step Documentation**: Detailed descriptions for each test step with visual confirmation
- **Professional Presentation**: Modern, responsive design suitable for stakeholder review

### WebDriver Management
- **WebDriverManager Integration**: Automatic driver download and management
- **No Manual Setup**: ChromeDriver automatically downloaded and configured
- **Version Compatibility**: Automatically matches driver version to browser version
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **WebDriverWait Support**: Built-in 10-second timeout for explicit waits

---

**Last Updated**: October 2025  
**Java Version**: 11+ (OpenJDK)  
**Maven Version**: 3.9.11  
**Selenium Version**: 4.23.0  
**WebDriverManager Version**: 5.6.3  
**Test Status**: ✅ All Tests Passing (2 Test Classes)  
**Report Status**: 📊 Beautiful HTML Reports with Screenshots Generated
