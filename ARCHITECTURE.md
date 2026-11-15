# Architecture and execution flow

## High-level structure

```text
TestNG suite (testng.xml)
        |
        +--> Test classes
        |      +--> UI tests ------> Page objects ------> Selenium WebDriver
        |      +--> API tests -----> REST Assured ------> External sample API
        |      +--> DB tests ------> DBUtils/JDBC ------> MySQL test database
        |
        +--> TestListener -------> Extent Reports
        |
        +--> BaseTest/DriverFactory -> ThreadLocal WebDriver lifecycle
```

## Main components

### Driver and test lifecycle

- `DriverFactory` creates Chrome or Firefox drivers based on `browser` in configuration.
- The driver is stored in a `ThreadLocal<WebDriver>`, allowing separate browser instances for parallel TestNG test threads.
- `BaseTest` creates the driver and explicit wait before each test method and quits the driver afterward.
- CI adds headless Chrome flags when the `CI` environment variable is present.

### Page objects

Page classes under `src/main/java/com/vilayat/pages` encapsulate GreenKart page interactions. Tests should assert business outcomes rather than duplicate low-level locators and browser operations.

### Utilities and reporting

- `ConfigReader` reads the application properties and supports environment overrides for database keys.
- `WaitUtils` centralizes explicit wait operations.
- `ScreenshotUtils` captures browser evidence for failed tests.
- `TestListener` records TestNG pass/fail outcomes in Extent Reports and attaches failure screenshots when a WebDriver is available.
- `DBUtils` provides JDBC query/update helpers and test-data synchronization functions.

### API tests

API tests live under `src/test/java/com/vilayat/tests` and use the API base and model classes under `src/test/java/com/vilayat/api`. The current sample tests exercise GET and POST operations, response status/data assertions, response-time checks, POJO mapping and JSON schema validation against an external sample API.

### Database tests

The database tests use a MySQL database separate from the public GreenKart web application. The GitHub Actions workflow starts a MySQL 8 service, creates `greenkart_db.products`, seeds three product rows, and supplies the JDBC connection settings through environment variables.

This is a controlled test database used for JDBC and cross-layer test exercises. It is not the database backing the public GreenKart website.

## Suite and parallelism

`testng.xml` defines the suite and runs classes in parallel with a thread count of four. This makes thread-safety of all shared test resources important. In particular, the current `DBUtils` uses one static JDBC connection; this is a known limitation and should be refactored to use scoped connections before relying on concurrent database tests.

## CI path

The validated pipeline is `.github/workflows/maven.yml`:

1. Start MySQL 8 as a service container.
2. Check out the repository.
3. Set up Temurin JDK 21 (the Maven compiler targets Java 17).
4. Create and seed the test database.
5. Run `mvn clean test`.
6. Upload `target/surefire-reports/` even when tests fail.

The GitHub Actions workflow is the only CI path for which this repository currently links a successful full-suite run. The Jenkinsfile and GitLab pipeline are present but require separate validation; do not treat their presence as proof that those pipelines currently run successfully.
