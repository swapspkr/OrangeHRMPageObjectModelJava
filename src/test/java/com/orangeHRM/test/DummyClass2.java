package com.orangeHRM.test;

import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass2 extends BaseClass{

	@Test
	public void verifyPageTitle() {
		ExtentManager.startTest(" DummyClass2 Test");
		String actualTitle = getDriver().getTitle();
		assert actualTitle.equals("OrangeHRM"):"Test Failed .Title not matching";
		System.out.println("Page Title :-" +actualTitle);
		ExtentManager.logStep("Validation successfully");	
	}
}
