package com.orangeHRM.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;

public class LoginPageTest extends BaseClass {
	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}

	@Test
	public void verifyValidLoginTest(String username, String password) {

		loginPage.login("Admin", "admin123");
		Assert.assertTrue(homePage.isAdminTabVisible(),"Admin tab should be visible after successfull login ");
		homePage.logout();
		staticWait(5);
	}
}
