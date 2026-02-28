package com.prasad_v.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.prasad_v.logging.CustomLogger;
import com.prasad_v.logging.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages ExtentTest instances for each test thread to support parallel execution.
 * Provides methods to log test steps, attach screenshots, add API request/response details, etc.
 */
public class ExtentTestManager {
    private static final CustomLogger logger = LogManager.getLogger(ExtentTestManager.class);
    private static final Map<Long, ExtentTest> testMap = new HashMap<>();
    private static final ThreadLocal<String> testNameThread = new ThreadLocal<>();
    private static final ExtentReports extent = ExtentReportManager.getInstance();
    private static final String SCREENSHOT_FOLDER = "test-output/screenshots/";

    static {
        createScreenshotFolder();
    }

    /**
     * Create the screenshot folder if it doesn't exist
     */
    private static void createScreenshotFolder() {
        File screenshotDir = new File(SCREENSHOT_FOLDER);
        if (!screenshotDir.exists()) {
            boolean created = screenshotDir.mkdirs();
            if (created) {
                logger.info("Created screenshot directory: " + screenshotDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create screenshot directory: " + screenshotDir.getAbsolutePath());
            }
        }
    }

    /**
     * Get the ExtentTest instance for the current thread
     *
     * @return The ExtentTest instance
     */
    public static synchronized ExtentTest getTest() {
        return testMap.get(Thread.currentThread().getId());
    }

    /**
     * Get the name of the current test
     *
     * @return The test name
     */
    public static String getTestName() {
        return testNameThread.get();
    }

    /**
     * Start a new test and associate it with the current thread
     *
     * @param testName The name of the test
     * @param description The test description
     * @return The created ExtentTest instance
     */
    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testMap.put(Thread.currentThread().getId(), test);
        testNameThread.set(testName);
        logger.info("Started test: " + testName);
        return test;
    }

    /**
     * Create a new node under the current test
     *
     * @param nodeName The name of the node
     * @param description The node description
     * @return The created ExtentTest node
     */
    public static synchronized ExtentTest createNode(String nodeName, String description) {
        ExtentTest node = getTest().createNode(nodeName, description);
        testMap.put(Thread.currentThread().getId(), node);
        logger.info("Created node: " + nodeName + " under test: " + getTestName());
        return node;
    }

    /**
     * Remove the test from the thread map
     */
    public static synchronized void endTest() {
        extent.flush();
        testMap.remove(Thread.currentThread().getId());
        testNameThread.remove();
    }

    /**
     * Log a passed step with message
     *
     * @param message The step message
     */
    public static void logPass(String message) {
        getTest().log(Status.PASS, message);
        logger.info("TEST PASS: " + message);
    }

    /**
     * Log a failed step with message
     *
     * @param message The step message
     */
    public static void logFail(String message) {
        getTest().log(Status.FAIL, message);
        logger.error("TEST FAIL: " + message);
    }

    /**
     * Log a failed step with message and exception details
     *
     * @param message The step message
     * @param t The exception
     */
    public static void logFail(String message, Throwable t) {
        getTest().log(Status.FAIL, message + "\n" + t.getMessage());
        getTest().log(Status.FAIL, t);
        logger.error("TEST FAIL: " + message, t);
    }

    /**
     * Log a skipped step with message
     *
     * @param message The step message
     */
    public static void logSkip(String message) {
        getTest().log(Status.SKIP, message);
        logger.info("TEST SKIP: " + message);
    }

    /**
     * Log an informational step with message
     *
     * @param message The step message
     */
    public static void logInfo(String message) {
        getTest().log(Status.INFO, message);
        logger.info("TEST INFO: " + message);
    }

    /**
     * Log a warning step with message
     *
     * @param message The step message
     */
    public static void logWarning(String message) {
        getTest().log(Status.WARNING, message);
        logger.warn("TEST WARNING: " + message);
    }

    /**
     * Attach a screenshot to the report
     *
     * @param screenshotPath The path to the screenshot file
     * @param title The title for the screenshot
     */
    public static void attachScreenshot(String screenshotPath, String title) {
        try {
            getTest().log(Status.INFO, title,
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            logger.info("Attached screenshot: " + screenshotPath);
        } catch (Exception e) {
            logWarning("Failed to attach screenshot: " + e.getMessage());
            logger.error("Failed to attach screenshot", e);
        }
    }

    /**
     * Create a screenshot from byte array and attach it to the report
     *
     * @param imageBytes The screenshot as byte array
     * @param title The title for the screenshot
     * @return The path to the saved screenshot
     */
    public static String attachScreenshot(byte[] imageBytes, String title) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = getTestName().replaceAll("\\s+", "_") + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_FOLDER + fileName;

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            }

            attachScreenshot(filePath, title);
            return filePath;
        } catch (Exception e) {
            logWarning("Failed to save and attach screenshot: " + e.getMessage());
            logger.error("Failed to save and attach screenshot", e);
            return null;
        }
    }

    /**
     * Log an API request with details
     *
     * @param endpoint The API endpoint
     * @param method The HTTP method
     * @param headers The request headers
     * @param body The request body
     */
    public static void logRequest(String endpoint, String method, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("<details>")
                .append("<summary><b>API Request Details</b></summary>")
                .append("<p><b>Endpoint:</b> ").append(endpoint).append("</p>")
                .append("<p><b>Method:</b> ").append(method).append("</p>");

        if (headers != null && !headers.isEmpty()) {
            sb.append("<p><b>Headers:</b></p>")
                    .append("<pre>").append(headers).append("</pre>");
        }

        if (body != null && !body.isEmpty()) {
            // Try to format JSON if it appears to be JSON
            if (body.trim().startsWith("{") || body.trim().startsWith("[")) {
                sb.append("<p><b>Body:</b></p>")
                        .append("<div>");
                getTest().log(Status.INFO, sb.toString());
                logJson(body);
                getTest().log(Status.INFO, "</div></details>");
            } else {
                sb.append("<p><b>Body:</b></p>")
                        .append("<pre>").append(body).append("</pre>")
                        .append("</details>");
                getTest().log(Status.INFO, sb.toString());
            }
        } else {
            sb.append("</details>");
            getTest().log(Status.INFO, sb.toString());
        }

        logger.info("Logged API request details for: " + method + " " + endpoint);
    }

    /**
     * Log an API response with details
     *
     * @param statusCode The response status code
     * @param responseTime The response time in milliseconds
     * @param headers The response headers
     * @param body The response body
     */
    public static void logResponse(int statusCode, long responseTime, String headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("<details>")
                .append("<summary><b>API Response Details</b></summary>")
                .append("<p><b>Status Code:</b> ").append(statusCode).append("</p>")
                .append("<p><b>Response Time:</b> ").append(responseTime).append(" ms</p>");

        if (headers != null && !headers.isEmpty()) {
            sb.append("<p><b>Headers:</b></p>")
                    .append("<pre>").append(headers).append("</pre>");
        }

        if (body != null && !body.isEmpty()) {
            // Try to format JSON if it appears to be JSON
            if (body.trim().startsWith("{") || body.trim().startsWith("[")) {
                sb.append("<p><b>Body:</b></p>")
                        .append("<div>");
                getTest().log(Status.INFO, sb.toString());
                logJson(body);
                getTest().log(Status.INFO, "</div></details>");
            } else {
                sb.append("<p><b>Body:</b></p>")
                        .append("<pre>").append(body).append("</pre>")
                        .append("</details>");
                getTest().log(Status.INFO, sb.toString());
            }
        } else {
            sb.append("</details>");
            getTest().log(Status.INFO, sb.toString());
        }

        // Log status color based on response code
        Status logStatus = statusCode >= 200 && statusCode < 300 ? Status.PASS :
                (statusCode >= 300 && statusCode < 400 ? Status.WARNING : Status.FAIL);

        String statusMessage = "Response Status: " + statusCode + " (" +
                (logStatus == Status.PASS ? "Success" :
                        logStatus == Status.WARNING ? "Redirection" : "Error") + ")";

        getTest().log(logStatus, statusMessage);

        logger.info("Logged API response details with status code: " + statusCode);
    }

    /**
     * Log JSON data with syntax highlighting
     *
     * @param json The JSON string
     */
    public static void logJson(String json) {
        try {
            Markup markup = MarkupHelper.createCodeBlock(json, CodeLanguage.JSON);
            getTest().log(Status.INFO, markup);
        } catch (Exception e) {
            getTest().log(Status.INFO, json);
            logger.warn("Failed to format JSON, logging as plain text. " + e.getMessage());
        }
    }

    /**
     * Log XML data with syntax highlighting
     *
     * @param xml The XML string
     */
    public static void logXml(String xml) {
        try {
            Markup markup = MarkupHelper.createCodeBlock(xml, CodeLanguage.XML);
            getTest().log(Status.INFO, markup);
        } catch (Exception e) {
            getTest().log(Status.INFO, xml);
            logger.warn("Failed to format XML, logging as plain text. " + e.getMessage());
        }
    }

    /**
     * Log test step with a colored label
     *
     * @param status The step status
     * @param message The step message
     * @param color The label color
     */
    public static void logWithColor(Status status, String message, ExtentColor color) {
        Markup markup = MarkupHelper.createLabel(message, color);
        getTest().log(status, markup);
    }

    /**
     * Add a category to the current test
     *
     * @param category The category name
     */
    public static void assignCategory(String category) {
        getTest().assignCategory(category);
        logger.info("Assigned category: " + category + " to test: " + getTestName());
    }

    /**
     * Add an author to the current test
     *
     * @param author The author name
     */
    public static void assignAuthor(String author) {
        getTest().assignAuthor(author);
        logger.info("Assigned author: " + author + " to test: " + getTestName());
    }

    /**
     * Add a device to the current test
     *
     * @param device The device name
     */
    public static void assignDevice(String device) {
        getTest().assignDevice(device);
        logger.info("Assigned device: " + device + " to test: " + getTestName());
    }

    /**
     * Add a table to the report
     *
     * @param data The table data as 2D array
     */
    public static void logTable(String[][] data) {
        Markup markup = MarkupHelper.createTable(data);
        getTest().log(Status.INFO, markup);
        logger.info("Added table to test: " + getTestName());
    }
}
