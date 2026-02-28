# API Automation RestAssured (in Java)

#### Author - Prasad Valiv

API Automation Framework for CRUD and end-to-end API workflows.

## Quick Start

- Linux/macOS: `./mvnw test -DsuiteXmlFile=testng.xml -Denv=dev`
- Windows: `mvnw.cmd test -DsuiteXmlFile=testng.xml -Denv=dev`

## Tech Stack

1. Java (JDK 22)
2. Rest Assured + TestNG
3. Apache POI
4. AssertJ
5. Jackson + Gson
6. Log4j2
7. Allure + Extent Reports
8. Maven Wrapper
9. Jenkins Pipeline

## Runbook

### Compile

- Linux/macOS: `./mvnw clean compile`
- Windows: `mvnw.cmd clean compile`

### Test Suites

- Full suite: `./mvnw test -DsuiteXmlFile=testng.xml -Denv=dev`
- Regression suite: `./mvnw test -DsuiteXmlFile=testng_reg.xml -Denv=dev`
- E2E suite: `./mvnw test -DsuiteXmlFile=testng_E2E.xml -Denv=dev`

### Retry Listener Verification

- `./mvnw test -DsuiteXmlFile=testng_retry_check.xml -Denv=dev`

Expected behavior:
- First attempt fails intentionally.
- Retry listener triggers rerun.
- Second attempt passes.

## Reports

### Allure

- Serve report: `allure serve allure-results`
- Generate static report: `allure generate allure-results --clean -o allure-report`

### CI Verify Command

- `./mvnw verify -DsuiteXmlFile=testng.xml -Denv=dev`

## CI/CD

The pipeline file is available at `Jenkinsfile`.
Use the `ENV` and `SUITE` parameters to select target environment and suite XML.

## Environment Governance

For `qa` and `prod`, framework startup now validates configuration and fails fast if:
- URLs are missing or still use placeholder domains (like `example.com`)
- Required auth values are missing

### Required Variables

QA:
- `AUTH_CLIENT_ID`
- `AUTH_CLIENT_SECRET`
- `AUTH_USERNAME`
- `AUTH_PASSWORD`

PROD:
- `PROD_CLIENT_ID`
- `PROD_CLIENT_SECRET`
- `PROD_API_USERNAME`
- `PROD_API_PASSWORD`

Optional SSL:
- `SSL_KEYSTORE_PASSWORD` (qa)
- `PROD_KEYSTORE_PASSWORD` (prod)

Use `.env.example` as the template for your local/CI secret setup.
