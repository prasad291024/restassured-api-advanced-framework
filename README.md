# API Automation REST Assured Framework (Java)

#### Author: Prasad Valiv
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5.5.5-green.svg)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.11.0-blue.svg)](https://testng.org/)
[![Checkstyle](https://img.shields.io/badge/Checkstyle-0%20Violations-brightgreen.svg)](checkstyle.xml)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](pom.xml)

Production-grade, modular, and secure REST Assured API automation testing framework built for high performance, maintainability, parallel execution, and strict CI/CD quality gates.

---

## ⚡ Quick Start

Execute the default test suite (27 tests) immediately without configuring parameters:

```bash
# Windows
mvnw.cmd test

# Linux / macOS
./mvnw test
```

Run dedicated defensive security and authentication boundary tests:
```bash
mvnw.cmd test -Dtest=SecurityTests
```

> [!NOTE]
> **PowerShell Users**: On Windows PowerShell, quote arguments containing `=` (e.g. `.\mvnw.cmd test "-DsuiteXmlFile=testng_reg.xml"`).

---

## 🛠️ Technology Stack

* **Language**: Java 21 LTS (`--release 21`, verified across JDK 21 LTS & JDK 25 LTS runtimes)
* **Core API Client**: REST Assured 5.5.5
* **Test Runner**: TestNG 7.11.0 (with ThreadLocal parallel execution & `RetryAnalyzer`)
* **Serialization & Data**: Jackson 2.19.0, Gson 2.12.1, Java Faker 1.0.2, Apache POI 5.4.0
* **Assertions**: AssertJ 3.27.3, TestNG assertions
* **Observability & Logging**: Log4j2 2.24.3, CustomLogger, comprehensive `LogSanitizer`
* **Reporting**: Allure 2.29.1, ExtentReports 5.1.2 (ThreadLocal)
* **Static Analysis**: Checkstyle 3.4.0 (0 violations), SpotBugs 4.8.6.5
* **Mock Virtualization**: MockServer 5.15.0
* **CI/CD & Containers**: GitHub Actions, Jenkinsfile, Dockerfile

---

## 📚 Complete Documentation Suite

All detailed architectural, operational, and security guides are located in the [docs/](docs/) folder:

| Document | Description |
| :--- | :--- |
| **[Architecture & Design Guide](docs/architecture.md)** | Multi-tier abstraction layers, design patterns, component relationships, and package layout. |
| **[Test Execution Guide & Runbook](docs/execution-guide.md)** | Suite options, CLI parameter overrides, environment switching, and reporting commands. |
| **[Security & Secret Management Guide](docs/security.md)** | Credential resolution hierarchy, Base64 prefix format, and automated log/report sanitization. |
| **[Comprehensive Security Audit Report](docs/security-audit.md)** | 11-point security audit findings, CVE analysis, risk classification, and remediation details. |
| **[Parallel Execution Architecture](docs/PARALLEL_EXECUTION.md)** | Thread pool configuration, suite parallelization strategies, and state isolation mechanics. |
| **[CI/CD Workflows](docs/CI_CD_WORKFLOWS.md)** | GitHub Actions pipelines, quality gate enforcement, caching strategies, and Allure deployment. |
| **[Troubleshooting & Debugging Guide](docs/troubleshooting.md)** | Diagnostic steps for HTTP status codes (403, 404, 405, 503), parallel collisions, and IDE issues. |
| **[Modernization & Improvement Log](docs/improvement-log.md)** | Chronological log of refactorings, before-vs-after comparisons, and test execution metrics. |
| **[Architecture & Evolution Roadmap](docs/plan.md)** | Multi-phase strategic roadmap from foundation hardening to virtualization and security maturity. |

---

## 🧪 Test Suites & Execution Matrix

| Suite File | Description | Test Count | Recommended Usage | Command |
| :--- | :--- | :---: | :--- | :--- |
| **`testng.xml`** | Default mixed suite (E2E flows + CRUD + Security) | 27 | Baseline runs & nightly builds | `mvnw.cmd test` |
| **`testng_reg.xml`** | Complete regression suite (all CRUD + Integration + Security) | 15 | Release qualification | `mvnw.cmd test "-DsuiteXmlFile=testng_reg.xml"` |
| **`testng_parallel.xml`** | Multi-threaded parallel suite (classes + methods) | 15 | Fast pull request validations | `mvnw.cmd test "-DsuiteXmlFile=testng_parallel.xml"` |
| **`SecurityTests`** | Authorization boundaries, bad credentials & sanitization | 8 | Security regression gate | `mvnw.cmd test -Dtest=SecurityTests` |
| **`testng_E2E.xml`** | Dedicated end-to-end integration workflows | 8 | Core business journey tests | `mvnw.cmd test "-DsuiteXmlFile=testng_E2E.xml"` |
| **`testng_retry_check.xml`** | Transient failure & retry listener verification | 1 | Testing retry behavior | `mvnw.cmd test "-DsuiteXmlFile=testng_retry_check.xml"` |

---

## 🔒 Security & Data Masking Highlights

1. **Zero Hardcoded Secrets**: All environment configurations strictly use dynamic properties and environment variable syntax (`${DEV_AUTH_USERNAME:admin}`, `${DEV_AUTH_PASSWORD:password123}`).
2. **Explicit Base64 Prefixing**: Standardized on `base64:<encoded-string>` prefix to prevent corrupted plaintext passwords.
3. **Automated Redaction (`LogSanitizer`)**: Real-time regex scrubbing of passwords, authorization headers, session cookies, Bearer tokens, query parameters, credit card numbers, and SSNs across both logs and Allure report attachments.
4. **Header Blacklisting**: RestAssured internal `LogConfig` blacklists sensitive headers (`Authorization`, `Cookie`, `token`, `X-API-Key`) as `[BLACK-LISTED]`.
5. **Strict TLS Enforcement**: HTTPS enforcement with standard JVM TLS verification enabled across all environments.

---

## 📊 Test Reporting

### Allure Reports
Allure results are automatically generated in `allure-results/`:
```bash
# Serve interactive report in browser
allure serve allure-results

# Build standalone HTML report bundle
allure generate allure-results --clean -o target/allure-report
```

### ExtentReports
Interactive HTML dashboards are automatically produced after every run:
* **Location**: `test-output/ExtentReports/API-Automation-Report_<timestamp>.html`

---

## 🔍 Code Quality & Static Analysis

```bash
# Run Checkstyle audit (enforces 0 violations)
mvnw.cmd checkstyle:check

# Generate HTML Checkstyle report
mvnw.cmd checkstyle:checkstyle

# Run SpotBugs static analysis
mvnw.cmd spotbugs:check
```

---

## 🐳 Docker Execution

Run tests inside an isolated containerized environment:

```bash
# Build the Docker image
docker build -t restassured-framework .

# Run the default test suite
docker run --rm restassured-framework

# Run the regression suite with custom environment
docker run --rm restassured-framework mvn test "-DsuiteXmlFile=testng_reg.xml" -Denv=dev
```

---

## 🔄 CI/CD Automation

Three automated GitHub Actions workflows are included in `.github/workflows/`:
1. **API Framework CI (`ci.yml`)**: Triggered on push/PR with strict quality gates (failures break the build).
2. **Publish Allure Report (`publish-report.yml`)**: Deploys Allure HTML reports to GitHub Pages.
3. **Scheduled Regression (`regression.yml`)**: Runs `testng_reg.xml` on a daily schedule at 2 AM UTC.
