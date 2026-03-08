package com.orangeHRM.test;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DBConnection;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class DBVerificationTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setupPages() {
		loginPage = new LoginPage(getDriver());
		homePage = new HomePage(getDriver());
	}
	
	
	@Test(dataProvider="emplVerification", dataProviderClass=DataProviders.class)
	public void verifyEmployeeNameVerificationFromDB(String empID,String empName) {
		
		SoftAssert softAssert = getSoftAssert();
		ExtentManager.logStep("Login with Admin credentials.");
		loginPage.login(prop.getProperty("username"), prop.getProperty("password"));

		ExtentManager.logStep("Click on PIM tab.");
		homePage.clickPIM();

		ExtentManager.logStep("Search for Employee.");
		homePage.employeeSearch(empName);

		ExtentManager.logStep("Get the Employee Name from DB");
		String employee_id = empID;

		Map<String, String> empDetails = DBConnection.getEmployeeDetails(employee_id);

		String empFirstName = empDetails.get("firstname");
		String empMiddleName = empDetails.get("middlename");
		String emplLastName = empDetails.get("lastname");

		String emplFirstAndMiddleName = (empFirstName + " " + 
		        (empMiddleName != null ? empMiddleName : "")).trim();

		// Validation for first and middle name
		ExtentManager.logStep("Verify the employee first and middle name");
		softAssert.assertTrue(homePage.verifyEmployeeFirstAndMiddleName(emplFirstAndMiddleName),
				"First and Middle name are not Matching");
		
		//validation for last name
		ExtentManager.logStep("Verify the employee last name");
		softAssert.assertTrue(homePage.verifyEmployeeLastName(emplLastName));
		
		ExtentManager.logStep("DB Validation Completed");

		softAssert.assertAll();
	}
}
