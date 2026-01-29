package com.orangehrm.actiondriver;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;

	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
	}

	// Wait for element to be clickable
	private void waitforElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			System.out.println("Element is not clickable : " + e.getMessage());
		}
	}

	// Wait for element to be visible
	private void waitforElementToBeVisible(By by) {

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			System.out.println("Element not visible: " + e.getMessage());
		}
	}

	// Method to click an element
	public void click(By by) {
		try {
			waitforElementToBeClickable(by);
			driver.findElement(by).click();
		} catch (Exception e) {
			System.out.println("Unable to click element : " + e.getMessage());
		}
	}

	// method to enter text in input field 
	public void enterText(By by, String value) {
		try {
			waitforElementToBeVisible(by);
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
		} catch (Exception e) {
			System.out.println("Unable to enter the value : " + e.getMessage());
		}
	}

	public String getText(By by) {
		try {
			waitforElementToBeVisible(by);
			return driver.findElement(by).getText();
		} catch (Exception e) {
			System.out.println("Unable to get the Text : " + e.getMessage());
		}
		return "";
	}
	
	// Method to compare text
	public boolean compareText(By by, String expectedText) {
		try {
			waitforElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				System.out.println("Text are matching :" + actualText + "equals" + expectedText);
				return true;
			} else {
				System.out.println("Text are not matching :" + actualText + "equals" + expectedText);
				return false;
			}
		} catch (Exception e) {
			System.out.println("Unable to compare text : " + e.getMessage());
			return false;
		}
	}
	
	//Method to check if an element is displayed
	public boolean isDisplayed(By by) {
		try {
			waitforElementToBeVisible(by);
			return driver.findElement(by).isDisplayed();	 
		} catch (Exception e) {
			System.out.println("Element is not displayed : " + e.getMessage());
			return false;	
		}
	}
	
	// Scroll to an element -- Added a semicolon ; at the end of the script string
		public void scrollToElement(By by) {
			try {
				//applyBorder(by,"green");
				JavascriptExecutor js = (JavascriptExecutor) driver;
				WebElement element = driver.findElement(by);
				js.executeScript("arguments[0].scrollIntoView(true);", element);
			} catch (Exception e) {
				//applyBorder(by,"red");
				//logger.error("Unable to locate element:" + e.getMessage());
			}
		}
	
	// Wait for the page to load
		public void waitForPageLoad(int timeOutInSec) {
			try {
				wait.withTimeout(Duration.ofSeconds(timeOutInSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
						.executeScript("return document.readyState").equals("complete"));
				//logger.info("Page loaded successfully.");
			} catch (Exception e) {
			}
		}

}
