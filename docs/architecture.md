# Framework Architecture & Design Guide

## 1. Architectural Overview

The framework is a modular, multi-layered API automation testing solution built on Java 22/25, REST Assured, TestNG, and Maven. It follows separation of concerns, decoupling test logic from HTTP communication, authentication, configuration, data generation, and reporting.

```mermaid
graph TD
    subgraph Test_Layer [Test Layer]
        TestNG_Suites[TestNG XML Suites]
        Test_Classes[Test Classes: Integration & CRUD]
        BaseTest[BaseTest Setup & Teardown]
    end

    subgraph Service_Layer [Service & Client Layer]
        BookingService[BookingService API Abstraction]
        RequestBuilder[RequestBuilder Spec Builder]
        API_Endpoints[API & Endpoint Constants]
    end

    subgraph Config_Security [Config & Security Layer]
        ConfigMgr[ConfigurationManager: CLI & Env]
        SecureConfigMgr[SecureConfigManager: Base64 & Secrets]
        Properties[dev.properties / qa.properties]
    end

    subgraph Data_Layer [Data & Modeling Layer]
        POJOs[POJOs: Booking, Auth, BookingDates]
        Builders[BookingBuilder Test Data Generator]
        DataProviders[Excel / JSON DataProviders]
    end

    subgraph Interception_Reporting [Cross-Cutting & Reporting Layer]
        LoggingFilter[LoggingFilter & LogSanitizer]
        TestExecutionListener[TestExecutionListener & RetryAnalyzer]
        AllureReport[Allure Reports & Attachments]
        ExtentManager[ExtentTestManager & ExtentReports]
    end

    TestNG_Suites --> Test_Classes
    Test_Classes --> BaseTest
    Test_Classes --> BookingService
    Test_Classes --> Builders
    BaseTest --> ConfigMgr
    ConfigMgr --> SecureConfigMgr
    SecureConfigMgr --> Properties
    BookingService --> RequestBuilder
    BookingService --> POJOs
    RequestBuilder --> LoggingFilter
    LoggingFilter --> LogSanitizer
    Test_Classes --> TestExecutionListener
    TestExecutionListener --> AllureReport
    TestExecutionListener --> ExtentManager
```

---

## 2. Core Architectural Layers

### 2.1 Service Layer (`com.prasad_v.services`)
* **`BookingService`**: Encapsulates all domain-specific HTTP endpoints (`createBooking`, `getBooking`, `updateBooking`, `partialUpdateBooking`, `deleteBooking`, `getBookingIds`).
* **Isolation**: Tests do not write raw REST Assured queries or manually assemble URLs. They interact with high-level service methods returning typed `Response` objects or deserialized POJOs.
* **Benefits**: If an API endpoint or payload structure changes, updates are localized to the service class rather than across dozens of test methods.

### 2.2 Core Communication & Request Specification (`com.prasad_v.core`)
* **`RequestBuilder`**: Uses the Builder pattern to construct standard `RequestSpecification` instances with pre-configured Base URI, content types, headers, authorization, query/path parameters, and custom filters.
* **`API` & `Endpoint`**: Central repository for base paths, paths, and resource URIs (`/booking`, `/auth`, `/ping`), eliminating hardcoded strings.
* **`LoggingFilter`**: Intercepts outgoing requests and incoming responses, logging payloads via Log4j2 while routing through `LogSanitizer` to prevent sensitive credentials or PII leaks.

### 2.3 Base Test Lifecycle (`com.prasad_v.tests.base.BaseTest`)
* **Thread-Safe Specs**: Manages thread-local `RequestSpecification` so parallel test classes execute in complete isolation without state leakage.
* **Service Instantiation**: Provides access to pre-configured `BookingService` instances across all extending tests.
* **Authentication Helper (`getToken`)**: Issues dynamic token requests using isolated, single-use `RequestSpecification` instances without mutating the shared test specification.

### 2.4 Configuration & Security (`com.prasad_v.config`)
* **`ConfigurationManager`**: Loads environment properties with a hierarchical precedence:
  1. CLI System Properties (`-Dkey=value`)
  2. Operating System Environment Variables (`KEY=VALUE`)
  3. Environment properties files (`src/test/resources/config/<env>.properties`)
* **`SecureConfigManager`**: Handles credential retrieval and supports explicit encoded secrets using the `base64:` prefix without corrupting standard plaintext strings.

### 2.5 Data Modeling & Builders (`com.prasad_v.models` / `com.prasad_v.builders`)
* **Lombok & Jackson POJOs**: Strong type definitions for `AuthPayload`, `TokenResponse`, `Booking`, `BookingResponse`, and `BookingDates`.
* **`BookingBuilder`**: Generates randomized, realistic mock test data on demand using Java Faker, ensuring tests avoid data collision under parallel execution.

### 2.6 Listeners, Interceptors & Reporting (`com.prasad_v.listeners` / `com.prasad_v.reporting`)
* **`TestExecutionListener`**: Implements TestNG's `ITestListener` to automatically capture test start, success, failure, and skip events.
* **Sanitized Attachments**: On test failure, attaches sanitized request, response, and stack traces to Allure without exposing tokens or secrets.
* **`ExtentTestManager`**: Uses `ThreadLocal<ExtentTest>` to ensure ExtentReports test nodes are thread-safe during multi-threaded execution.
* **`RetryAnalyzer`**: Supports automatic re-execution of transiently failing tests.

---

## 3. Design Patterns Employed

| Pattern | Implementation Class | Purpose |
| :--- | :--- | :--- |
| **Builder** | `RequestBuilder`, `BookingBuilder` | Fluent, clean instantiation of complex HTTP requests and test payloads. |
| **Service Layer** | `BookingService` | Decouples test assertions from underlying HTTP transport logic. |
| **Singleton / Static Manager** | `ConfigurationManager`, `ExtentManager` | Centralized, thread-safe configuration and report lifecycle management. |
| **Template Method** | `BaseTest` | Standardizes setup (`@BeforeMethod`), teardown (`@AfterMethod`), and context management. |
| **Observer** | `TestExecutionListener` | Listens to TestNG lifecycle events to trigger logging, sanitization, and report updates. |
| **Interceptor / Filter** | `LoggingFilter` | Hooks into REST Assured execution pipeline to sanitize and log payloads. |

---

## 4. Package Structure

```
src
├── main
│   ├── java/com/prasad_v
│   │   ├── config/              # ConfigurationManager, SecureConfigManager
│   │   ├── constants/           # Framework constants & timeouts
│   │   ├── core/                # RequestBuilder, API & Endpoint constants
│   │   ├── filters/             # LoggingFilter, LogSanitizer
│   │   ├── listeners/           # TestExecutionListener, RetryAnalyzer
│   │   ├── logging/             # CustomLogger & Log4j2 integration
│   │   ├── models/              # Jackson/Lombok POJOs
│   │   ├── reporting/           # ExtentManager, ExtentTestManager
│   │   ├── services/            # BookingService & business operations
│   │   ├── utils/               # Excel, JSON, Schema & Assertions utils
│   │   └── builders/            # BookingBuilder payload generator
│   └── resources/
│       ├── log4j2.xml           # Logging configuration
│       └── schemas/             # JSON Schema validation files
└── test
    ├── java/com/prasad_v/tests
    │   ├── base/                # BaseTest setup and shared helpers
    │   ├── crud/                # Individual endpoint unit & CRUD tests
    │   └── integration/         # Multi-step E2E workflows and assignments
    └── resources/
        ├── config/              # dev.properties, qa.properties, etc.
        └── testdata/            # Excel & JSON test data files
```
