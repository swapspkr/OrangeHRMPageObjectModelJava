package com.orangehrm.actiondriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
			// System.out.println("Element is not clickable : " + e.getMessage());
		}
	}

	// Wait for element to be visible
	private void waitforElementToBeVisible(By by) {

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			// System.out.println("Element not visible: " + e.getMessage());
			logger.error("Element is not visible  : " + e.getMessage());
		}
	}

	// Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		applyBorder(by, "green");
		try {
			waitforElementToBeClickable(by);
			driver.findElement(by).click();
			ExtentManager.logStep("Clicked an element." + elementDescription);
			logger.info("Clicked an element." + elementDescription);
		} catch (Exception e) {
			applyBorder(by, "red");
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click element :",
					elementDescription + "_unable to click.");
			logger.error("Unable to click element : " + e.getMessage());
		}
	}

	// method to enter text in input field
	public void enterText(By by, String value) {
		try {
			waitforElementToBeVisible(by);
			applyBorder(by, "green");
			WebElement element = driver.findElement(by);
			element.clear();
			element.sendKeys(value);
			logger.info("Entered text on " + getElementDescription(by) + "--> " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to enter the value : " + e.getMessage());
		}
	}

	public String getText(By by) {
		try {
			waitforElementToBeVisible(by);
			applyBorder(by, "green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			applyBorder(by, "red");
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
				applyBorder(by, "green");
				logger.info("Text are matching :" + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Text verified successfully!",
						actualText + " Equals " + expectedText);
				return true;
			} else {
				applyBorder(by, "red");
				logger.error("Text are not matching :" + actualText + " , " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparison failed!",
						actualText + " Not Equals " + expectedText);
				return false;
			}
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to compare text : " + e.getMessage());
			return false;
		}
	}

	// Method to check if an element is displayed
	public boolean isDisplayed(By by) {
		try {
			waitforElementToBeVisible(by);
			applyBorder(by, "green");
			logger.info("Element is displayed" + getElementDescription(by));
			ExtentManager.logStep("Element is displayed: " + getElementDescription(by));
			ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Element is displayed !",
					"Element is displayed:" + getElementDescription(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Element is not displayed : " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Element not displayed: ",
					"Element not displayed:" + getElementDescription(by));
			return false;
		}
	}

	// Scroll to an element -- Added a semicolon ; at the end of the script string
	public void scrollToElement(By by) {
		try {
			applyBorder(by, "green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			applyBorder(by, "red");
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
		return "Unable to describe element due to error: ";
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

	// Utility method to apply border to Element
	public void applyBorder(By by, String color) {
		try {
			WebElement element = driver.findElement(by);
			String script = "arguments[0].style.border='3px solid " + color + "'";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			logger.info("Applied the border with color" + color + "to element :" + getElementDescription(by));
		} catch (Exception e) {
			logger.warn("Failed to apply the border to an element" + getElementDescription(by), e);
			e.printStackTrace();
		}
	}

	// ***** SELECT METHODS ********

	public void SelectByVisibleText(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByVisibleText(value);
			applyBorder(by, "green");
			logger.info("Select dropdown Value:" + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.info("Unable to select dropdown value :" + value, e);
		}

	}

	public void SelectByValue(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByValue(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by actual value" + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.info("Unable to select dropdown by value :" + value, e);
		}
	}

	public void SelectByIndex(By by, int index) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByIndex(index);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by index :" + index);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.info("Unable to select dropdown by index :" + index, e);
		}
	}

	public void getDropdownOptions(By by) {
		List<String> optionsList = new ArrayList<>();
		try {
			WebElement dropdownELement = driver.findElement(by);
			Select select = new Select(dropdownELement);

			for (WebElement option : select.getOptions()) {
				optionsList.add(option.getText());
			}
			applyBorder(by, "green");
			logger.info("Retrive dropdown option for :" + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.info("Unable to get dropdown options :" + e.getMessage());
		}
	}

	// Click using Javascript

	public void clickUsingJS(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			applyBorder(by, "green");
			logger.info("Clicked element using JavaScript:" + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.info("Unable to click using JavaScript:" + e);
		}

	}

	public void scrollToBottom() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
		} catch (Exception e) {
			logger.error("Unable to scroll to bottom: " + e.getMessage());
		}
	}

	public void highlightElement(By by) {

		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
			logger.info(" Hightlight element using JavaScript:" + getElementDescription(by));

		} catch (Exception e) {
			logger.error("Unable to hightlight element using JavaScript" + e);

		}
	}

	public void switchToWindow(String windowTitle) {
		try {
			Set<String> windows = driver.getWindowHandles();
			for (String window : windows) {
				driver.switchTo().window(window);
				if (driver.getTitle().contains(windowTitle)) {
					logger.info("Switched to window: " + windowTitle);
					return;
				}
			}
			logger.error("Window not found: " + windowTitle);
		} catch (Exception e) {
			logger.error("Unable to switch window: " + e.getMessage());
		}
	}
	
	public void switchToFrame(By by) {
	    try {
	        WebElement frame = driver.findElement(by);
	        driver.switchTo().frame(frame);
	        logger.info("Switched to frame: " + getElementDescription(by));
	    } catch (Exception e) {
	        logger.error("Unable to switch to frame: " + e.getMessage());
	    }
	}
	
	public void switchToDefaultContent() {
	    try {
	        driver.switchTo().defaultContent();
	        logger.info("Switched back to main page");
	    } catch (Exception e) {
	        logger.error("Unable to switch to default content: " + e.getMessage());
	    }
	}
	
	/// ****** Handling Alert ********
	public void acceptAlert() {
	    try {
	        driver.switchTo().alert().accept();
	        logger.info("Alert accepted successfully");
	    } catch (Exception e) {
	        logger.error("Unable to accept alert: " + e.getMessage());
	    }
	}
	
	public void dismissAlert() {
	    try {
	        driver.switchTo().alert().dismiss();
	        logger.info("Alert dismissed successfully");
	    } catch (Exception e) {
	        logger.error("Unable to dismiss alert: " + e.getMessage());
	    }
	}
	
	public String getAlertText() {
		String alertText = "";
		try {
			alertText = driver.switchTo().alert().getText();
			logger.info("Alert text: " + alertText);
		} catch (Exception e) {
			logger.error("Unable to get alert text: " + e.getMessage());
		}
		return alertText;
	}
	
	// ********** Browser Actions **********
	
	public void refreshPage() {
	    try {
	        driver.navigate().refresh();
	        ExtentManager.logStep("Page refreshed successfully");
	        logger.info("Page refreshed successfully");
	    } catch (Exception e) {
	        logger.error("Unable to refresh page: " + e.getMessage());
	        ExtentManager.logStep("Unable to refresh page:"+e.getMessage());
	    }
	}
	
	public String getCurrentURL() {
	    try {
	        String url = driver.getCurrentUrl();
	        logger.info("Current URL is: " + url);
	        ExtentManager.logStep("Current url fetched :"+url);
	        return url;
	    } catch (Exception e) {
	        logger.error("Unable to get current URL: " + e.getMessage());
	        ExtentManager.logStep("Unable to get current URL:" + e.getMessage());
	        return null;
	    }
	}
	
	public void maximizeWindow() {
	    try {
	        driver.manage().window().maximize();
	        logger.info("Browser window maximized successfully");
	        ExtentManager.logStep("Browser window maximized successfully");
	    } catch (Exception e) {
	        logger.error("Unable to maximize browser window: " + e.getMessage());
	        ExtentManager.logFailure(BaseClass.getDriver(),"Unable to maximize browser window","Failed to maximized browser.");
	    }
	}
	
	
	// ******** Advance Actions *******
	
	public void moveToElement(By by) {
	    try {
	        WebElement element = driver.findElement(by);
	        Actions actions = new Actions(driver);
	        actions.moveToElement(element).perform();
	        logger.info("Moved to element: " + getElementDescription(by));
	        ExtentManager.logStep("Mouse hovered on element: " + getElementDescription(by));
	    } catch (Exception e) {
	        logger.error("Unable to move to element: " + e.getMessage());
	        ExtentManager.logStep("Unable to move to element: " + e.getMessage());
	    }
	}
	
	public void dragAndDrop(By sourceBy, By targetBy) {
	    try {
	        WebElement source = driver.findElement(sourceBy);
	        WebElement target = driver.findElement(targetBy);
	        Actions actions = new Actions(driver);
	        actions.dragAndDrop(source, target).perform();
	        logger.info("Dragged element from " + getElementDescription(sourceBy) + 
	                    " to " + getElementDescription(targetBy));
	        ExtentManager.logStep("Dragged element from " + getElementDescription(sourceBy) + 
                    " to " + getElementDescription(targetBy));
	    } catch (Exception e) {
	        logger.error("Unable to perform drag and drop: " + e.getMessage());
	        ExtentManager.logStep("Unable to perform drag and drop: " + e.getMessage());
	    }
	}
	
	public void doubleClick(By by) {
	    try {
	        WebElement element = driver.findElement(by);
	        Actions actions = new Actions(driver);
	        actions.doubleClick(element).perform();
	        logger.info("Double click performed on element: " + getElementDescription(by));
	        ExtentManager.logStep("Double click performed on element: " + getElementDescription(by));
	    } catch (Exception e) {
	        logger.error("Unable to perform double click: " + e.getMessage());
	        ExtentManager.logStep("Unable to perform double click: " + e.getMessage());
	    }
	}
	
	public void rightClick(By by) {
	    try {
	        WebElement element = driver.findElement(by);
	        Actions actions = new Actions(driver);
	        actions.contextClick(element).perform();
	        logger.info("Right click performed on element: " + getElementDescription(by));
	        ExtentManager.logStep("Right click performed on element: " + getElementDescription(by));
	    } catch (Exception e) {
	        logger.error("Unable to perform right click: " + e.getMessage());
	        ExtentManager.logStep("Unable to perform right click: " + e.getMessage());
	    }
	}
	
	public void sendKeyWithAction(By by, String value) {
	    try {
	        WebElement element = driver.findElement(by);
	        Actions actions = new Actions(driver);
	        actions.moveToElement(element)
	               .click()
	               .sendKeys(value)
	               .build()
	               .perform();
	        logger.info("Value '" + value + "' entered using Actions on element: " + getElementDescription(by));
	        ExtentManager.logStep("Value entered using Actions: " + value);
	    } catch (Exception e) {
	        logger.error("Unable to send keys using Actions: " + e.getMessage());
	        ExtentManager.logStep("Unable to send keys using Actions: " + e.getMessage());
	    }
	}
	
	public void clearText(By by) {
	    try {
	        WebElement element = driver.findElement(by);
	        element.clear();
	        logger.info("Text cleared from element: " + getElementDescription(by));
	        ExtentManager.logStep("Text cleared from element: " + getElementDescription(by));
	    } catch (Exception e) {
	        logger.error("Unable to clear text: " + e.getMessage());
	        ExtentManager.logStep("Unable to clear text: " + e.getMessage());
	    }
	}
	
	public void uploadFile(By by, String filePath) {
	    try {
	        WebElement element = driver.findElement(by);
	        element.sendKeys(filePath);
	        logger.info("File uploaded successfully: " + filePath);
	        ExtentManager.logStep("File uploaded: " + filePath);
	    } catch (Exception e) {
	        logger.error("Unable to upload file: " + e.getMessage());
	        ExtentManager.logStep("Unable to upload file: " + e.getMessage());
	    }
	}
}
