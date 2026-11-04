
package com.example.tests;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LocalLoginTest {

    WebDriver driver;

    int passed = 0;
    int failed = 0;

    public static void main(String[] args) {

        LocalLoginTest test = new LocalLoginTest();

        System.out.println("================================");
        System.out.println("SELENIUM AUTOMATED TEST EXECUTION");
        System.out.println("================================");

        test.runTest("Valid Login Test",
                test::validLoginTest);

        test.runTest("Invalid Login Test",
                test::invalidLoginTest);

        test.runTest("Background Color Test",
                test::checkBackgroundColor);

        System.out.println("\n================================");
        System.out.println("TEST EXECUTION SUMMARY");
        System.out.println("================================");

        System.out.println("Tests Passed: " + test.passed);
        System.out.println("Tests Failed: " + test.failed);

        if (test.failed > 0) {
            throw new AssertionError("Some Selenium tests failed.");
        }

        System.out.println("ALL TESTS PASSED SUCCESSFULLY!");
    }

    // Execute each test independently
    public void runTest(String name, Runnable test) {

        try {

            setUp();

            test.run();

            passed++;

            System.out.println("[PASS] " + name);

        } catch (Exception | AssertionError e) {

            failed++;

            System.out.println("[FAIL] " + name);
            e.printStackTrace();

        } finally {

            tearDown();
        }
    }

    // Launch Chrome and open the local HTML page
    public void setUp() {

        ChromeOptions options = new ChromeOptions();
        String chromeBinary = System.getenv("CHROME_BIN");

        if (chromeBinary != null && !chromeBinary.isBlank()) {
            options.setBinary(chromeBinary);
        }
        String chromeDriver = System.getenv("CHROMEDRIVER_BIN");

        if (chromeDriver != null && !chromeDriver.isBlank()) {
            System.setProperty("webdriver.chrome.driver", chromeDriver);
        }

        // Enable headless mode when running in GitHub Actions
        if (Boolean.getBoolean("headless")) {

            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");

        }

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        URL page = getClass()
                .getClassLoader()
                .getResource("webpage/login.html");

        if (page == null) {

            throw new IllegalStateException(
                    "login.html was not found in project resources."
            );

        }

        driver.get(page.toExternalForm());
    }

    // Test 1: Valid login
    public void validLoginTest() {

        driver.findElement(By.id("username"))
                .sendKeys("student");

        driver.findElement(By.id("password"))
                .sendKeys("12345");

        driver.findElement(By.id("loginButton"))
                .click();

        String message = driver.findElement(By.id("message"))
                .getText();

        System.out.println("Valid Login Message: " + message);

        assertEquals(
                "Login Successful",
                message,
                "Valid login verification failed."
        );
    }

    // Test 2: Invalid login
    public void invalidLoginTest() {

        driver.findElement(By.id("username"))
                .sendKeys("wrong");

        driver.findElement(By.id("password"))
                .sendKeys("wrong");

        driver.findElement(By.id("loginButton"))
                .click();

        String message = driver.findElement(By.id("message"))
                .getText();

        System.out.println("Invalid Login Message: " + message);

        assertEquals(
                "Invalid Username or Password",
                message,
                "Invalid login verification failed."
        );
    }

    // Test 3: Background color verification
    public void checkBackgroundColor() {

        String bgColor = driver.findElement(By.id("loginBox"))
                .getCssValue("background-color");

        System.out.println("Background Color: " + bgColor);

        assertEquals(
                "rgba(173, 216, 230, 1)",
                bgColor,
                "Background color verification failed."
        );
    }

    // Simple Java assertion method
    public void assertEquals(
            String expected,
            String actual,
            String message) {

        if (!expected.equals(actual)) {

            throw new AssertionError(
                    message + " Expected: " + expected
                    + ", Actual: " + actual
            );

        }
    }

    // Close the browser after every test
    public void tearDown() {

        if (driver != null) {

            driver.quit();

            driver = null;
        }
    }
}
