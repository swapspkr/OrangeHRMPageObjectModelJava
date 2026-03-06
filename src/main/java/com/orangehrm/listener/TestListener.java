package com.orangehrm.listener;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class TestListener implements ITestListener{

	
	// Trigger when test start
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();	
		// Start logging in Extent Report
		ExtentManager.startTest(testName); 
		ExtentManager.logStep("Test Started :"+testName);
	}

	// Trigger when test success
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logStepWithScreenShot(BaseClass.getDriver(), "Test Passed Successfully !", "Test End: " + testName + " - ✅ Test Passed");
	}

	// Trigger when test success
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String failureMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(failureMessage);
		ExtentManager.logFailure(BaseClass.getDriver(), "Test Failed !", "Test End: " + testName + " - ❌ Red icon - Test Failed");
	}

	// Trigger when test skip
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test Skipped!"+testName);
	}

	// This will triggered when Suites starts
	@Override
	public void onStart(ITestContext context) {
		ExtentManager.getReporter(); // Initialize extent report

	}
	// trigger when suite ends
	@Override
	public void onFinish(ITestContext context) {
		ExtentManager.endTest();  // Flush extent report
	}

}
