package com.orangeHRM.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginPageTest extends BaseClass {
	private LoginPage loginPage;
	private HomePage homePage;

	
	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test(dataProvider="validLoginData",dataProviderClass=DataProviders.class)
	public void verifyValidLoginTest(String username,String password) {
		//ExtentManager.startTest("Valid Login Test"); -- This has been implemented in TestListener
		loginPage.login(username, password);
		ExtentManager.logStep("Navigating to login page entering username and password");
		ExtentManager.logStep("Verifying Admin tab is visible or not");
		Assert.assertTrue(homePage.isAdminTabVisible(),"Admin tab should be visible after successfull login ");
		ExtentManager.logStep("Validation successfully");
		homePage.logout();
		ExtentManager.logStep("Logged out successfully");
	}
	
	@Test(dataProvider="inValidLoginData",dataProviderClass=DataProviders.class)
	public void verifyInValidLoginTest(String username,String password) {
		//ExtentManager.startTest("Invalid Login Test"); -- This has been implemented in TestListener
		System.out.println("Running testMethod2 on thread: " + Thread.currentThread().getId());
		ExtentManager.logStep("Navigating to Login Page entering username and password");
		loginPage.login(username, password);
		String expectedErrorMessage = "Invalid credentials";
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage),"Test Failed: Invalid error message");
		ExtentManager.logStep("Validation Successful");	
	}
}
