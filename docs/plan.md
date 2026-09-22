# Java REST Assured Framework Architecture & Evolution Roadmap

## 1. Overview & Vision
The **REST Assured API Automation Framework** is designed for high-performance, maintainable, and secure API testing in Java. Built on top of **REST Assured**, **TestNG**, **Maven**, and **Allure/Extent Reports**, it provides a multi-tier abstraction layer separating test authoring, business services, network execution, and observability.

---

## 2. Framework Architecture & Phased Roadmap

### Phase 1: Foundation & Build Hardening (Completed ✅)
* **Maven Wrapper Standardized**: Cross-platform execution via `mvnw` and `mvnw.cmd`.
* **Zero-Parameter Default Execution**: Surefire configured with `<suiteXmlFile>testng.xml</suiteXmlFile>` default.
* **Pragmatic Static Analysis**: Configured `checkstyle.xml` enforcing naming, clean imports, and structural conventions without false-positive package blockers.
* **Java 22/25 Compatibility**: Handled compiler and runtime bytecode compatibility across contemporary LTS JDKs.

### Phase 2: Configuration & Security Hardening (Completed ✅)
* **Hierarchical Property Resolution**: Prioritized precedence:
  1. CLI System Properties (`-Dkey=value`)
  2. Operating System Environment Variables (`KEY=VALUE`)
  3. Environment-specific Properties file (`config/{env}.properties`)
* **Fail-Fast Environment Validation**: `EnvironmentConfigValidator` blocks insecure or placeholder domains in `qa` and `prod`.
* **Safe Secret Decoding**: Replaced ambiguous Base64 heuristic with explicit `base64:` prefix decoding, eliminating password corruption.
* **Observability Sanitization**: Real-time masking of passwords, access tokens, API keys, and PII via `LogSanitizer` in both console/file logs and Allure attachments.

### Phase 3: Parallel Execution & Thread Safety (Completed ✅)
* **Thread-Isolated Reporting**: `ExtentTestManager` backed by `ThreadLocal<ExtentTest>`, removing deprecated thread ID lookups and map locks.
* **Non-Mutating Base Test Specs**: Thread-safe request specification factories preventing cross-test pollution.
* **E2E Context Namespacing**: Context attributes isolated per flow (`flow1_bookingid`, `flow2_bookingid`) and guarded with `dependsOnMethods` to eliminate race conditions.
* **Parallel Execution Strategy**:
  - Independent CRUD operations parallelized across methods.
  - Multi-step E2E scenarios preserved in sequence and parallelized across classes/suites.

### Phase 4: Service Abstraction Layer (Current Phase)
* **Domain Service Expansion**: Utilize `BookingService`, `UserService`, and future domain services extending `BaseApiService`.
* **Contract & Schema Validation**: Automated JSON Schema verification using `SchemaValidator` against `src/test/resources/schemas/`.
* **Data Provider & Factory**: Dynamic payloads created via `BookingBuilder` and `JavaFaker`, complemented by Excel and JSON externalized data providers.

### Phase 5: Advanced Testing & CI/CD Maturity (Upcoming)
* **Mock Server Virtualization**: Dynamic test mocking via `MockServerManager` for third-party endpoints.
* **Performance Gate Integration**: `ResponseTimeValidator` threshold assertions integrated into regression pipelines.
* **Containerized Test Runs**: Dockerized Maven test execution containers for ephemeral CI environments.
