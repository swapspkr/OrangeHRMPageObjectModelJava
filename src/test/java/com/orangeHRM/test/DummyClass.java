package com.orangeHRM.test;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass extends BaseClass{

	@Test
	public void verifyPageTitle() {
		
		// Test checkin for webhook Test
		//ExtentManager.startTest("Dummy Test1"); -- This has been implemented in TestListener
		String actualTitle = getDriver().getTitle();
		ExtentManager.logStep("Verify Page Title");
		assert actualTitle.equals("OrangeHRM"):"Test Failed .Title not matching";
		System.out.println("Page Title :-" +actualTitle);
		ExtentManager.logSkip("This case is skipped.");
		throw new SkipException("Skipping the test as part of Test.");
	}
}
