package com.orangehrm.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	protected static Properties prop;
	protected FileInputStream fis;
	// protected static WebDriver driver;
	// private static ActionDriver actionDriver;
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new);
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

	public SoftAssert getSoftAssert() {
		return softAssert.get();
	}

	@BeforeSuite
	public void loadConfig() throws IOException {
		// load the configuration file
		prop = new Properties();
		fis = new FileInputStream(
				System.getProperty("user.dir") + File.separator + "src/main/resources/config.properties");
		prop.load(fis);
		logger.info("config.Properties file loaded");
		// Start extent report
		// ExtentManager.getReporter(); -- This has been implemented in TestListener
	}

	@BeforeMethod
	@Parameters("browser")
	public synchronized void setup(String browser) throws IOException {
		System.out.println("Setting up WebDriver for :" + this.getClass().getSimpleName());
		launchBrowser(browser);
		configueBrowser();
		staticWait(3);

		logger.info("WebDriver initialised and browser maximised");
		//logger.trace("This is trace message ");
		//logger.error("This is Error message");
		//logger.debug("This is debug message");

		// Initialize Action Driver instance once

		/*
		 * if (actionDriver == null) { actionDriver = new ActionDriver(driver);
		 * logger.info("Actiondriver instance created.--> "+Thread.currentThread().getId
		 * ()); }
		 */

		// Initialize ActionDriver for the current Thread
		actionDriver.set(new ActionDriver(getDriver()));
		logger.info("ActionDriver initlialized for thread: " + Thread.currentThread().getId());
	}
	
	// Initialize webdriver based on browser defined in config file

	private synchronized void launchBrowser(String browser) {

		boolean seleniumGrid = Boolean.parseBoolean(prop.getProperty("seleniumGrid"));
		String gridURL = prop.getProperty("gridURL");

		if (seleniumGrid) {
			try {
				if (browser.equalsIgnoreCase("chrome")) {
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("firefox")) {
					FirefoxOptions options = new FirefoxOptions();
					options.addArguments("-headless");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("edge")) {
					EdgeOptions options = new EdgeOptions();
					options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else {
					throw new IllegalArgumentException("Browser Not Supported: " + browser);
				}
				logger.info("RemoteWebDriver instance created for Grid in headless mode");
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid Grid URL", e);
			}
		} else {

			if (browser.equalsIgnoreCase("chrome")) {
				WebDriverManager.chromedriver().setup();
				// Create ChromeOptions
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless"); // Run Chrome in headless mode
				options.addArguments("--disable-gpu"); // Disable GPU for headless mode
				options.addArguments("--window-size=1920,1080"); // Set window size
				options.addArguments("--disable-notifications"); // Disable browser notifications
				options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
				options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

				// driver = new ChromeDriver();
				driver.set(new ChromeDriver(options)); // New Changes as per Thread
				ExtentManager.RegisterDriver(getDriver());
				logger.info("ChromeDriver Instance is created.");
				
			} else if (browser.equalsIgnoreCase("firefox")) {
				WebDriverManager.firefoxdriver().setup();
				// Create FirefoxOptions
				FirefoxOptions options = new FirefoxOptions();
				options.addArguments("--headless"); // Run Firefox in headless mode
				options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
				options.addArguments("--width=1920"); // Set browser width
				options.addArguments("--height=1080"); // Set browser height
				options.addArguments("--disable-notifications"); // Disable browser notifications
				options.addArguments("--no-sandbox"); // Needed for CI/CD environments
				options.addArguments("--disable-dev-shm-usage"); // Prevent crashes in low-resource environments

				// driver = new FirefoxDriver();
				driver.set(new FirefoxDriver(options)); // New Changes as per Thread
				ExtentManager.RegisterDriver(getDriver());
				logger.info("FirefoxDriver Instance is created.");
				
			} else if (browser.equalsIgnoreCase("edge")) {
				WebDriverManager.edgedriver().setup();
				EdgeOptions options = new EdgeOptions();
				options.addArguments("--headless"); // Run Edge in headless mode
				options.addArguments("--disable-gpu"); // Disable GPU acceleration
				options.addArguments("--window-size=1920,1080"); // Set window size
				options.addArguments("--disable-notifications"); // Disable pop-up notifications
				options.addArguments("--no-sandbox"); // Needed for CI/CD
				options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes

				// driver = new EdgeDriver();
				driver.set(new EdgeDriver(options)); // New Changes as per Thread
				ExtentManager.RegisterDriver(getDriver());
				logger.info("EdgeDriver Instance is created.");
			} else {
				throw new IllegalArgumentException("Browser Not Supported:" + browser);
			}
		}
	}

	// Configure Browser settings like implicitWait maximize browser and navigate to
	// url

	private void configueBrowser() {
		// implicit wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		boolean seleniumGrid = Boolean.parseBoolean(System.getProperty("seleniumGrid", prop.getProperty("seleniumGrid")));
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		// maximize the browser
		//getDriver().manage().window().maximize();
		// navigate to url

		/*
		 * try { getDriver().get(prop.getProperty("url")); } catch (Exception e) { //
		 * System.out.println("Failed to navigate to url" + e.getMessage());
		 * logger.error("Failed to navigate to url" + e.getMessage()); }
		 */
		
		if (seleniumGrid) {
			getDriver().get(prop.getProperty("url_grid"));
		} else {
			getDriver().get(prop.getProperty("url_local"));
		}
	}

	@AfterMethod
	public synchronized void tearDown() {
		if (getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				System.out.println("Unable to quit driver" + e.getMessage());
			}
		}
		logger.info("WebDriver instance is closed");
		driver.remove();
		actionDriver.remove();
		// To flush the extent report
		// ExtentManager.endTest(); -- This has been implemented in TestListener
	}

	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

	/*
	 * // Driver getter method public static WebDriver getDriver() { return driver;
	 * }
	 */
	public static Properties getProp() {
		return prop;
	}

	// Getter method for driver
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			System.out.println("Webdriver instance not initialise");
			throw new IllegalStateException("Webdriver instance not initialise");
		}
		return driver.get();
	}

	// Getter method for Actiondriver
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			System.out.println("ActionDriver instance not initialise");
			throw new IllegalStateException("ActionDriver instance not initialise");
		}

		return actionDriver.get();
	}

	/// Driver setter method
	public void setDriver(ThreadLocal<WebDriver> driver) {
		this.driver = driver;
	}

}
