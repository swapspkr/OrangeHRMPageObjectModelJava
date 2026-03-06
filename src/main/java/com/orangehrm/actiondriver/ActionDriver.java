package com.orangehrm.actiondriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;

	public static final Logger logger = BaseClass.logger;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		logger.info("WebDriver Instance created");
	}

	// Wait for element to be clickable
	private void waitforElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element is not clickable  : " + e.getMessage());
			//System.out.println("Element is not clickable : " + e.getMessage());
		}
	}

	// Wait for element to be visible
	private void waitforElementToBeVisible(By by) {

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			//System.out.println("Element not visible: " + e.getMessage());
			logger.error("Element is not visible  : " + e.getMessage());
		}
	}

	// Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			waitforElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("Clicked an element."+elementDescription);
			logger.info("Clicked an element."+elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element :", elementDescription+"_unable to click.");
			logger.error("Unable to click element : " + e.getMessage());
		}
	}

	// method to enter text in input field
	public void enterText(By by, String value) {
		try {
			waitforElementToBeVisible(by);
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text on "+getElementDescription(by) +"--> "+ value);
		} catch (Exception e) {
			logger.error("Unable to enter the value : " + e.getMessage());
		}
	}

	public String getText(By by) {
		try {
			waitforElementToBeVisible(by);
			return driver.findElement(by).getText();
		} catch (Exception e) {
			logger.error("Unable to get the Text : " + e.getMessage());
		}
		return "";
	}

	// Method to compare text
	public boolean compareText(By by, String expectedText) {
		try {
			waitforElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				logger.info("Text are matching :" + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Text verified successfully!", actualText+" Equals "+expectedText);
				return true;
			} else {
				logger.error("Text are not matching :" + actualText + " , " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparison failed!", actualText+" Not Equals "+expectedText);
				return false;
			}
		} catch (Exception e) {
			logger.error("Unable to compare text : " + e.getMessage());
			return false;
		}
	}

	// Method to check if an element is displayed
	public boolean isDisplayed(By by) {
		try {
			waitforElementToBeVisible(by);
			logger.info("Element is displayed"+getElementDescription(by));
			ExtentManager.logStep("Element is displayed: "+getElementDescription(by));
			ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Element is displayed !", "Element is displayed:"+getElementDescription(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			logger.error("Element is not displayed : " +e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(),"Element not displayed: ","Element not displayed:"+getElementDescription(by));
			return false;
		}
	}

	// Scroll to an element -- Added a semicolon ; at the end of the script string
	public void scrollToElement(By by) {
		try {
			// applyBorder(by,"green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			// applyBorder(by,"red");
			logger.error("Unable to locate element:" + e.getMessage());
		}
	}

	// Wait for the page to load
	public void waitForPageLoad(int timeOutInSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete"));
			logger.info("Page loaded successfully.");
		} catch (Exception e) {
			logger.error("Page not loaded  : " + e.getMessage());
		}
	}

	// Method to get the description of an element using By locator
	public String getElementDescription(By locator) {

		// Check for null driver or locator to avoid NullPointerException
		if (driver == null) {
			return "Driver is not initialized.";
		}
		if (locator == null) {
			return "Locator is null.";
		}

		try {
			// Find the element using the locator
			WebElement element = driver.findElement(locator);

			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String placeholder = element.getDomAttribute("placeholder");
			String type = element.getDomAttribute("type");
			String className = element.getDomAttribute("class");
			String text = element.getDomAttribute("text");
			
			// Return a description based on available attributes
			if (isNotEmpty(name)) {
				return "Element with name: " + name;
			} else if (isNotEmpty(id)) {
				return "Element with ID: " + id;
			} else if (isNotEmpty(text)) {
				return "Element with text: " + truncate(text, 50);
			} else if (isNotEmpty(className)) {
				return "Element with class: " + className;
			} else if (isNotEmpty(placeholder)) {
				return "Element with placeholder: " + placeholder;
			} else {
				return "Element located using: " + locator.toString();
			}
		} catch (Exception e) {
			logger.error("Unable to describe element due to error: " + e.getMessage());
		}
		return "Unable to describe element due to error: " ;
	}
	
	// Utility method to truncate long strings
	private String truncate(String value, int maxLength) {
		if (value == null || value.length() <= maxLength) {
			return value;
		}
		return value.substring(0, maxLength) + "...";
	}

	// Utility method to check if a string is not null or empty
	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}
}
