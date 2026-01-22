package com.orangehrm.pages;

import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;

public class DummyClass extends BaseClass{

	@Test
	public void verifyPageTitle() {
		String actualTitle = driver.getTitle();
		assert actualTitle.equals("OrangeHRM"):"Test Failed .Title not matching";
		System.out.println("Page Title :-" +actualTitle);
	}
}
