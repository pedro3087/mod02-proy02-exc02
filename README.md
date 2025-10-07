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
- **JavaScript alerts handling (Alert, Confirm, Prompt)**
- **iFrame handling and interaction**
- **Organized package structure by functionality**

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
                    ├── base/
                    │   └── BaseTest.java                    # Base test class with WebDriverManager & WebDriverWait
                    ├── tests/
                    │   ├── ecommerce/
                    │   │   └── InventoryFlowTest.java       # E-commerce flow testing
                    │   └── windows/
                    │       └── WindowsAlertsFramesFormTest.java # Windows and alerts handling
                    ├── utils/
                    │   ├── ReportGenerator.java             # HTML report generator
                    │   ├── ScreenshotUtil.java              # Screenshot capture utility
                    │   └── TestHelper.java                  # Test utilities and failure handling
                    └── webtesting/                          # Future general web tests
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
# Run e-commerce flow test
mvn test -Dtest=InventoryFlowTest

# Run windows and alerts handling test
mvn test -Dtest=WindowsAlertsFramesFormTest

# Run specific test method
mvn test -Dtest=WindowsAlertsFramesFormTest#testWindows
mvn test -Dtest=WindowsAlertsFramesFormTest#testAlerts
mvn test -Dtest=WindowsAlertsFramesFormTest#testFrames
mvn test -Dtest=WindowsAlertsFramesFormTest#testFormAutomation
mvn test -Dtest=WindowsAlertsFramesFormTest#testFailureOnlyScreenshots

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

### Test Method: `testAlerts()`

**Test Steps**:
1. Navigate to `https://the-internet.herokuapp.com/javascript_alerts`
2. **JS Alert Test**: Click "Click for JS Alert" button and accept alert
3. **Alert Validation**: Verify result text is "You successfully clicked an alert"
4. **JS Confirm Test**: Click "Click for JS Confirm" button and dismiss alert
5. **Confirm Validation**: Verify result text is "You clicked: Cancel"
6. **JS Prompt Test**: Click "Click for JS Prompt" button
7. **Prompt Interaction**: Send "Hola QA" text and accept alert
8. **Prompt Validation**: Verify result text is "You entered: Hola QA"

**Alert Handling Strategies Demonstrated**:
- **Alert Detection**: `ExpectedConditions.alertIsPresent()` for reliable waiting
- **Alert Interaction**: `driver.switchTo().alert()` for alert access
- **Alert Actions**: `accept()`, `dismiss()`, and `sendKeys()` methods
- **Result Validation**: JUnit 5 assertions for all alert results
- **XPath Locators**: Precise button identification using text content
- **Console Logging**: Detailed output for debugging and verification

**Key Features**:
- **Three Alert Types**: Alert, Confirm, and Prompt handling
- **Robust Wait Strategy**: WebDriverWait for alert presence
- **User Input Simulation**: Text input in prompt alerts
- **Comprehensive Validation**: All alert results verified
- **Error Handling**: Proper wait conditions to avoid timeouts

### Test Method: `testFrames()`

**Test Steps**:
1. Navigate to `https://the-internet.herokuapp.com/iframe`
2. **iFrame Switching**: Use WebDriverWait to switch to iframe by ID
3. **Editor Interaction**: Find text editor element inside iframe
4. **Content Management**: Clear existing content and set new text using JavaScript
5. **Context Switching**: Switch back to main document using defaultContent()
6. **Validation**: Re-switch to iframe to verify content
7. **Content Assertion**: Assert that text equals "Text inside the frame"
8. **Cleanup**: Switch back to default content

**iFrame Handling Strategies Demonstrated**:
- **Frame Detection**: `ExpectedConditions.frameToBeAvailableAndSwitchToIt()` for reliable iframe access
- **Context Switching**: Proper switching between iframe and main document
- **JavaScript Integration**: Using `JavascriptExecutor` for TinyMCE editor interaction
- **Content Management**: `innerHTML` manipulation for reliable content setting
- **Element Location**: Finding elements within iframe context
- **Validation Strategy**: Re-switching to iframe for content verification

**Key Features**:
- **TinyMCE Compatibility**: Handles special TinyMCE editor behavior
- **JavaScript Execution**: Uses `innerHTML` for reliable content management

**Test Method**: `testFormAutomation()`

**Test Steps**:
1. Navigate to `https://www.selenium.dev/selenium/web/web-form.html`
2. **Text Input**: Fill text input field (name="my-text") with "Memo QA"
3. **Password Field**: Fill password field (name="my-password") with "Secreto123"
4. **Textarea**: Fill textarea field (name="my-textarea") with "Some comments for the form."
5. **Radio Button**: Click radio button "Option 2" using ID selector
6. **Checkbox**: Click checkbox with fallback for ElementClickInterceptedException
7. **Dropdown**: Select "Two" from dropdown using XPath
8. **Form Submission**: Click Submit button
9. **Confirmation Wait**: Wait for confirmation message to be visible
10. **Validation**: Assert confirmation text is "Received!"

**Form Automation Strategies Demonstrated**:
- **Field Clearing**: Using `clear()` before entering new text
- **Multiple Input Types**: Handling text, password, and textarea fields
- **Radio Button Selection**: Direct clicking on radio button elements
- **Checkbox Handling**: Try-catch pattern with JavaScript fallback
- **Dropdown Interaction**: Clicking dropdown and selecting specific options
- **Form Submission**: Complete end-to-end form submission
- **Success Validation**: Waiting for and asserting confirmation messages

**Key Features**:
- **Robust Locators**: Using ID, name, and XPath selectors appropriately
- **Error Handling**: JavaScript fallback for click interception issues
- **Form Validation**: Complete form submission workflow with confirmation
- **Multiple Element Types**: Text inputs, radio buttons, checkboxes, dropdowns

**Test Method**: `testFailureOnlyScreenshots()`

**Test Steps**:
1. Enable failure-only screenshot mode
2. Navigate to form page (no screenshot captured)
3. Fill form fields (no screenshot captured)
4. Intentionally cause failure (screenshot captured)
5. Continue after failure (screenshot captured)
6. Reset to default mode

**Failure-Only Screenshot Strategies Demonstrated**:
- **Mode Control**: `ScreenshotUtil.setFailureOnlyMode(true/false)`
- **Failure Marking**: `ScreenshotUtil.markFailure()` or `ScreenshotUtil.captureFailureScreenshot()`
- **Conditional Capture**: Screenshots only captured when failures occur
- **State Management**: Proper reset of failure state between tests
- **Helper Integration**: `TestHelper` class for easier failure handling
- **Context Management**: Proper iframe to main document switching
- **Robust Validation**: Comprehensive content verification
- **Error Handling**: Proper wait conditions and context management

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

### Package Organization

The project follows a clean, organized package structure by functionality:

**Package Structure**:
- **`com.example.base`**: Base test classes and common functionality
- **`com.example.tests.ecommerce`**: E-commerce specific test cases
- **`com.example.tests.windows`**: Windows and alerts handling tests
- **`com.example.utils`**: Utility classes for reporting and screenshots
- **`com.example.webtesting`**: Future general web testing (reserved)

**Benefits**:
- **Clear Separation**: Each package has a specific purpose
- **Easy Navigation**: Find tests by domain/functionality
- **Scalable**: Easy to add new test domains
- **Maintainable**: Related files grouped together
- **Team Collaboration**: Clear structure for multiple developers

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
- ✅ **JavaScript alerts handling (Alert, Confirm, Prompt) implemented**
- ✅ **iFrame handling and interaction implemented**
- ✅ **Form automation with multiple input types implemented**
- ✅ **Radio button, checkbox, and dropdown handling implemented**
- ✅ **Form submission and confirmation validation implemented**
- ✅ **Organized package structure by functionality**
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

### Enhanced Screenshot Management
- **Failure-Only Mode**: Capture screenshots only when test failures occur
- **Mode Control**: `ScreenshotUtil.setFailureOnlyMode(true/false)` for flexible screenshot behavior
- **Failure Marking**: `ScreenshotUtil.markFailure()` and `ScreenshotUtil.captureFailureScreenshot()` methods
- **State Management**: Automatic reset of failure state between tests
- **TestHelper Integration**: Simplified failure handling with `TestHelper` class
- **Conditional Capture**: Smart screenshot capture based on test state

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

### JavaScript Alerts Handling
- **Alert Types**: Support for Alert, Confirm, and Prompt dialogs
- **Alert Detection**: `ExpectedConditions.alertIsPresent()` for reliable waiting
- **Alert Actions**: `accept()`, `dismiss()`, and `sendKeys()` methods
- **User Input Simulation**: Text input in prompt alerts
- **Result Validation**: Comprehensive assertion testing for all alert results

### iFrame Handling
- **Frame Detection**: `ExpectedConditions.frameToBeAvailableAndSwitchToIt()` for reliable iframe access
- **Context Switching**: Proper switching between iframe and main document
- **JavaScript Integration**: Using `JavascriptExecutor` for complex editor interactions
- **Content Management**: `innerHTML` manipulation for reliable content setting
- **TinyMCE Compatibility**: Handles special TinyMCE editor behavior

### WebDriver Management
- **WebDriverManager Integration**: Automatic driver download and management
- **No Manual Setup**: ChromeDriver automatically downloaded and configured
- **Version Compatibility**: Automatically matches driver version to browser version
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **WebDriverWait Support**: Built-in 10-second timeout for explicit waits

### Package Organization
- **Functional Structure**: Organized by test type and functionality
- **Clear Separation**: Base classes, test classes, and utilities in separate packages
- **Scalable Design**: Easy to add new test domains and functionality
- **Maintainable Code**: Related files grouped together for better organization

---

**Last Updated**: October 2025  
**Java Version**: 11+ (OpenJDK)  
**Maven Version**: 3.9.11  
**Selenium Version**: 4.23.0  
**WebDriverManager Version**: 5.6.3  
**Test Status**: ✅ All Tests Passing (6 Test Methods in 2 Test Classes)  
**Report Status**: 📊 Beautiful HTML Reports with Screenshots Generated
