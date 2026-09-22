# Test Execution Guide & Runbook

This guide covers all options for running, configuring, and analyzing tests locally and in CI/CD environments.

---

## 1. Prerequisites

* **Java Development Kit**: JDK 21 LTS standard (`--release 21`). Verified and tested with both JDK 21 LTS and JDK 25 LTS.
* **Build Tool**: Maven 3.9+ or use the included Maven Wrapper (`./mvnw` or `mvnw.cmd`).
* **Allure CLI** (Optional, for local HTML report viewing): `npm install -g allure-commandline` or `scoop install allure`.

Verify Java version:
```bash
java -version
```

---

## 2. Test Execution Commands

### 2.1 Default Execution
Running `mvn test` without arguments defaults to executing `testng.xml` against the `dev` environment:
```bash
# Windows
mvnw.cmd test

# Linux / macOS
./mvnw test
```

### 2.2 Selecting a Specific TestNG Suite
Use `-DsuiteXmlFile=<filename>` to run specific test suites:

| Suite File | Description | Test Count | Recommended Usage |
| :--- | :--- | :---: | :--- |
| **`testng.xml`** | Default mixed suite (E2E flows + CRUD + Security) | 27 | Nightly builds and baseline runs |
| **`testng_parallel.xml`** | High-concurrency parallel suite (classes + methods) | 15 | Fast pull request validations |
| **`testng_reg.xml`** | Complete regression suite (all CRUD + Integration + Security) | 15 | Release qualification and scheduled regressions |
| **`testng_E2E.xml`** | Dedicated end-to-end integration workflows | 8 | Critical business path verification |
| **`testng_retry_check.xml`** | Flakiness and retry verification suite | 1 | Validating test retry analyzer logic |

> [!NOTE]
> **PowerShell Parameter Quoting**: In Windows PowerShell, CLI arguments containing `=` must be enclosed in quotes:
> ```powershell
> .\mvnw.cmd test "-DsuiteXmlFile=testng_reg.xml"
> ```

Examples:
```bash
# Run the complete regression suite (15 tests)
mvnw.cmd test "-DsuiteXmlFile=testng_reg.xml"

# Run dedicated defensive security tests (8 tests)
mvnw.cmd test -Dtest=SecurityTests

# Run the parallel suite
mvnw.cmd test "-DsuiteXmlFile=testng_parallel.xml"

# Run dedicated E2E flows
mvnw.cmd test "-DsuiteXmlFile=testng_E2E.xml"
```

### 2.3 Environment Selection
Switch target environments using `-Denv=<environment>`:

```bash
# Development environment (default: restful-booker demo)
mvnw.cmd test -Denv=dev

# QA environment
mvnw.cmd test -Denv=qa

# Staging environment
mvnw.cmd test -Denv=staging
```
Configuration files are loaded from `src/test/resources/config/<env>.properties`.

### 2.4 Overriding Configurations via CLI
Any property in `<env>.properties` can be overridden at runtime via `-D<key>=<value>`:

```bash
# Override Base URL
mvnw.cmd test -Dapi.base.url=https://custom-mock-server.internal

# Override credentials
mvnw.cmd test -Dapi.username=customAdmin -Dapi.password=customSecret123

# Override Base64-encoded secret
mvnw.cmd test -Dapi.password=base64:Y3VzdG9tUGFzczEyMw==
```

---

## 3. Code Quality & Linting Checks

Run Checkstyle to verify code formatting and standards:
```bash
mvnw.cmd checkstyle:check
```

Generate Checkstyle HTML report:
```bash
mvnw.cmd checkstyle:checkstyle
# Report generated at target/site/checkstyle.html
```

---

## 4. Test Reporting

### 4.1 Allure Reporting
Allure results are automatically written to `allure-results/` during test execution.

To start an interactive local Allure server:
```bash
allure serve allure-results
```

To build a standalone Allure HTML report bundle:
```bash
allure generate allure-results --clean -o target/allure-report
```

### 4.2 ExtentReports
ExtentReports HTML dashboards are generated automatically at the end of every run:
* Location: `target/extent-reports/`
* Open `target/extent-reports/index.html` in any web browser to view detailed execution graphs, logs, and timelines.

### 4.3 Surefire TestNG Reports
Standard Maven Surefire XML and HTML summaries are available at:
* `target/surefire-reports/testng-results.xml`
* `target/surefire-reports/index.html`

---

## 5. Running a Single Test Class or Method

Use the `-Dtest` property to run an isolated test class or method:

```bash
# Run a specific test class
mvnw.cmd test -Dtest=TestCreateBooking

# Run a specific test method within a class
mvnw.cmd test -Dtest=TestCreateBooking#testCreateBookingSuccess
```
*(Note: Ensure required base setup methods are preserved when executing single methods).*
