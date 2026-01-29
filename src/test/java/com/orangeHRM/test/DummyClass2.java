package com.orangeHRM.test;

import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;

public class DummyClass2 extends BaseClass{

	@Test
	public void verifyPageTitle() {
		String actualTitle = driver.getTitle();
		assert actualTitle.equals("OrangeHRM2"):"Test Failed .Title not matching";
		System.out.println("Page Title :-" +actualTitle);
	}
}
