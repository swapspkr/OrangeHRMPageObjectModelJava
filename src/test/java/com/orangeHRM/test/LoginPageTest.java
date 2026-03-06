package com.orangeHRM.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {
	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test
	public void verifyValidLoginTest() {
		ExtentManager.startTest("Valid Login Test");
		loginPage.login("Admin", "admin123");
		ExtentManager.logStep("Navigating to login page entering username and password");
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(),"Admin tab should be visible after successfull login ");
		ExtentManager.logStep("Validation successfully");
		homePage.logout();
		ExtentManager.logStep("Logged out successfully");
		staticWait(5);
	}
	
	@Test
	public void verifyInValidLoginTest() {
		ExtentManager.startTest("Invalid Login Test");
		loginPage.login("Admin", "admin1231");
		ExtentManager.logStep("Navigating to login page entering username and password");	
		String errorMsg = " Invalid Credentials1"; 
		Assert.assertTrue(loginPage.verifyErrorMessage(errorMsg));
		ExtentManager.logStep("Validation successfully");
		homePage.logout();
		ExtentManager.logStep("Logged out successfully");
		staticWait(5);
	}
}
