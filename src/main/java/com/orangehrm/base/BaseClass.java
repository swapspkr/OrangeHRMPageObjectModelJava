package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	protected static Properties prop;
	protected static WebDriver driver;

	@BeforeSuite
	public void loadConfig() throws IOException {
		// load the configuration file

		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis);
	}

	// Initialize webdriver based on browser defined in config file

	private void launchBrowser() {

		String browser = prop.getProperty("browser");

		switch (browser) {

		case "chrome":
			WebDriverManager.chromedriver().setup();
			ChromeOptions opts = new ChromeOptions();
			opts.addArguments("--start-maximized");
			// opts.addArguments("--headless=new"); // optional
			driver = new ChromeDriver(opts);
			break;
		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;
		case "edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			break;
		default:
			throw new IllegalArgumentException("Unsupported browser: " + browser);

		}
	}

	// Configure Browser settings like implicitWait maximize browser and navigate to
	// url

	private void configueBrowser() {
		// implicit wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		// maximize the browser
		driver.manage().window().maximize();
		// navigate to url

		try {
			driver.get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to navigate to url" + e.getMessage());
		}
	}

	@BeforeMethod
	public void setup() throws IOException {
		System.out.println("Setting up WebDriver for :" + this.getClass().getSimpleName());
		launchBrowser();
		configueBrowser();
		staticWait(3);
	}

	@AfterMethod
	public void tearDown() {
		if (driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				System.out.println("Unable to quit driver" + e.getMessage());
			}
		}
	}

	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	// Driver getter method
	public static WebDriver getDriver() {
		return driver;
	}

	// Driver setter method
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public static Properties getProp() {
		return prop;
	}

}
