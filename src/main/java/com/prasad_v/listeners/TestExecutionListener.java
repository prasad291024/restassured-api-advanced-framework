package com.prasad_v.listeners;

import com.prasad_v.reporting.ExtentReportManager;
import com.prasad_v.reporting.ExtentTestManager;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Field;

/**
 * Central TestNG listener for reporting lifecycle and failure diagnostics.
 */
public class TestExecutionListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription() != null ? result.getMethod().getDescription() : "";
        ExtentTestManager.startTest(testName, description);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.logPass("Test passed: " + result.getMethod().getMethodName());
        ExtentTestManager.endTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        String message = "Test failed: " + result.getMethod().getMethodName();
        if (throwable != null) {
            ExtentTestManager.logFail(message, throwable);
            Allure.addAttachment("Failure", throwable.toString());
        } else {
            ExtentTestManager.logFail(message);
        }
        attachResponseFromTestInstance(result);
        ExtentTestManager.endTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.logSkip("Test skipped: " + result.getMethod().getMethodName());
        ExtentTestManager.endTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
    }

    private void attachResponseFromTestInstance(ITestResult result) {
        Object instance = result.getInstance();
        if (instance == null) {
            return;
        }
        try {
            Field responseField = findField(instance.getClass(), "response");
            if (responseField == null) {
                return;
            }
            responseField.setAccessible(true);
            Object value = responseField.get(instance);
            if (value instanceof Response response) {
                Allure.addAttachment("Failure Response Body", "application/json", response.asString());
                ExtentTestManager.logInfo("Attached response body for failed test.");
            }
        } catch (Exception ignored) {
            ExtentTestManager.logWarning("Could not attach response body from failed test instance.");
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
