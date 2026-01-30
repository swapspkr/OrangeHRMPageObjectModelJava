package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.LoggerManager;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	protected static Properties prop;
	protected static WebDriver driver;
	private static ActionDriver actionDriver;
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	@BeforeSuite
	public void loadConfig() throws IOException {
		// load the configuration file

		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.Properties file loaded");
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
			logger.info("Chrome instance is initialized");
			break;
		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			logger.info("Firefox instance is initialized");
			break;
		case "edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			logger.info("Edge instance is initialized");
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
		
		logger.info("WebDriver initialised and browser maximised");
		logger.trace("This is trace message ");
		logger.error("This is Error message");
		logger.debug("This is debug message");
		
		// Initialize Action Driver instance once

		if (actionDriver == null) {
			actionDriver = new ActionDriver(driver);
			logger.info("Actiondriver instance created.");
		}
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
		logger.info("WebDriver instance is closed");
		driver = null;
		actionDriver = null;

	}

	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	/*
	 * // Driver getter method public static WebDriver getDriver() { return driver;
	 * }
	 * */
	public static Properties getProp() { return prop; }
	 

	// Getter method for driver
	public static WebDriver getDriver() {
		if (driver == null) {
			System.out.println("Webdriver instance not initialise");
			throw new IllegalStateException("Webdriver instance not initialise");
		}

		return driver;
	}

	// Getter method for driver
		public static ActionDriver getActionDriver() {
			if (actionDriver == null) {
				System.out.println("ActionDriver instance not initialise");
				throw new IllegalStateException("ActionDriver instance not initialise");
			}

			return actionDriver;
		}
	// Driver setter method
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

}
