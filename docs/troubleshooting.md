# Troubleshooting & Debugging Guide

This guide provides step-by-step solutions for common issues, error codes, and edge cases encountered during framework execution.

---

## 1. Common HTTP Error Codes & Root Causes

### 1.1 HTTP 403 Forbidden on PUT, PATCH, or DELETE
* **Symptom**: `TestUpdateBooking` or `TestDeleteBooking` fails with `403 Forbidden`.
* **Root Cause**: Missing, expired, or invalid auth token in cookie/header.
* **Troubleshooting Steps**:
  1. Verify the `/auth` endpoint credentials in `src/test/resources/config/<env>.properties`. For `restful-booker.herokuapp.com`, the username must be `admin` and password must be `password123`.
  2. Note that `restful-booker` returns HTTP 200 even for failed logins (with body `{"reason":"Bad credentials"}` and no token). Check if `getToken()` received a non-null token.
  3. Ensure the token is passed in the header as `Cookie: token=<token_value>`.

### 1.2 HTTP 405 Method Not Allowed
* **Symptom**: Endpoint responds with `405 Method Not Allowed`.
* **Root Cause**: Incorrect HTTP method used against the endpoint (e.g., calling `DELETE /booking` without appending the booking ID, or calling `POST` on an update-only route).
* **Troubleshooting Steps**:
  1. Inspect the service method in `BookingService.java` to verify the path parameters and HTTP verb.
  2. Confirm path parameter replacement (e.g., `/booking/{id}`).

### 1.3 HTTP 404 Not Found
* **Symptom**: `getBooking` or `updateBooking` returns `404 Not Found`.
* **Root Cause**:
  * The requested booking ID does not exist.
  * In parallel test execution, another thread deleted the booking before the current test read it.
* **Troubleshooting Steps**:
  1. In integration flows, ensure the booking creation step successfully recorded the generated booking ID.
  2. Avoid hardcoded booking IDs; always generate dynamic bookings per test instance using `BookingBuilder`.

### 1.4 HTTP 500 / 503 Service Unavailable / Gateway Timeout
* **Symptom**: Intermittent failures when communicating with external demo services.
* **Root Cause**: Free-tier cloud providers (like Heroku) spin down dynos after inactivity, causing 30-60 second cold-start delays or transient drops.
* **Troubleshooting Steps**:
  1. Use the `RetryAnalyzer` on critical tests to tolerate cold starts.
  2. Increase default timeouts in `FrameworkConstants.java`.

---

## 2. TestNG & Parallel Execution Issues

### 2.1 State Collisions Across Parallel Classes
* **Symptom**: Test flows pass individually when run sequentially, but fail when run in `testng_parallel.xml`.
* **Root Cause**: Shared state stored in static variables or un-namespaced `ITestContext` attributes.
* **Solution**:
  1. Store flow state in instance variables of the test class, or namespace keys in `ITestContext` (e.g., `context.setAttribute("flow1_bookingid", id)`).
  2. Use `dependsOnMethods` within the same class to guarantee execution ordering while keeping classes parallel.

### 2.2 Surefire Suite Parameter Error
* **Symptom**: `mvn test` outputs: `Suite file not found: ${suiteXmlFile}`.
* **Root Cause**: Maven Surefire plugin configured with `<suiteXmlFile>${suiteXmlFile}</suiteXmlFile>` without a default property defined in `<properties>`.
* **Solution**: Ensure `pom.xml` contains `<properties><suiteXmlFile>testng.xml</suiteXmlFile></properties>`.

---

## 3. Build & Static Analysis Issues

### 3.1 SpotBugs ClassFormatError on JDK 25
* **Symptom**: `java.lang.IllegalArgumentException: Unsupported class file major version 69`.
* **Root Cause**: SpotBugs ASM parser does not yet support Java 25 preview/LTS classfile version 69.
* **Solution**:
  1. In `pom.xml`, configure SpotBugs with `<failOnError>false</failOnError>`.
  2. Alternatively, run the build using JDK 21 or JDK 22.

### 3.2 Checkstyle Package Name Violations
* **Symptom**: Checkstyle fails with: `Name 'com.prasad_v' must match pattern '^[a-z]+(\.[a-z][a-z0-9]*)*$'`.
* **Root Cause**: Google Checks rule `PackageName` disallows underscores.
* **Solution**: Use the project's customized `checkstyle.xml` configuration, which accommodates legitimate project namespaces while enforcing strict naming, whitespace, and import standards.

---

## 4. Debugging & Logging Techniques

### 4.1 Enable Detailed Payload Logging
To inspect raw request and response payloads, check `logs/automation.log` or the console output.
* Requests and responses are formatted with headers, URIs, and pretty-printed JSON bodies.
* Tokens and passwords will be displayed as `[REDACTED]`.

### 4.2 Inspecting Allure Failure Details
When a test fails:
1. Open `allure-results/` or run `allure serve allure-results`.
2. Click the failed test case.
3. Review the attached **Request Payload**, **Response Payload**, and **Failure Stacktrace** tabs for the exact HTTP status code and response body returned by the server.
