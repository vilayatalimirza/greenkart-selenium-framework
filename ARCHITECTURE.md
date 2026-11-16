# Architecture and execution flow

## High-level structure

    GitHub Actions
          |
          +--> Maven Surefire
                   |
                   +--> testng.xml (default suite)
                   |       |
                   |       +--> 12 UI test classes
                   |       |       |
                   |       |       +--> Page objects
                   |       |                |
                   |       |                +--> Selenium WebDriver
                   |       |
                   |       +--> 1 Reqres API test class
                   |               |
                   |               +--> REST Assured --> Reqres sample API
                   |
                   +--> Surefire XML/text reports --> CI artifact

The isolated database CI job uses testng-db.xml and runs DatabaseSmokeTest and DatabaseCRUDTest against a disposable MySQL 8 service initialized from src/test/resources/init.sql. Three browser/database integration classes remain outside this isolated suite.

## Default TestNG suite scope

Maven Surefire is configured to execute testng.xml. The suite currently lists 13 classes and uses parallel class execution with a thread count of four:

- 12 UI classes that exercise the public GreenKart demo application.
- ProductApiTest, a legacy-named class that exercises Reqres user endpoints. It does not test a GreenKart backend API.

The following classes are present but excluded from the default suite:

- DatabaseCRUDTest
- DatabaseDrivenSearchTest
- DatabaseSmokeTest
- DatabaseSyncTest
- UItoDBValidationTest
- LoginViaAPITest (separate demo authentication website)

The 93 executions in the recorded workflow runs belong only to the configured suite.

## WebDriver and test lifecycle

- DriverFactory creates Chrome or Firefox drivers based on the browser setting.
- The WebDriver instance is stored in a ThreadLocal, allowing parallel TestNG classes to use separate browser instances.
- BaseTest creates the driver and explicit wait before each test method and quits the driver afterward.
- The GitHub Actions workflow sets the CI environment variable, enabling headless Chrome flags.

Only Chrome is represented by the recorded successful CI run. The Firefox branch in DriverFactory is not proof of validated Firefox coverage.

## Page objects

Classes under src/main/java/com/vilayat/pages encapsulate GreenKart page interactions. Tests should assert business outcomes rather than duplicate low-level locators and browser operations.

The country dropdown locator was changed from an exact inline-style XPath to a CSS selector for the select element. The new selector needs CI validation; the historical timeout remains in the run history.

## Utilities and reporting

- ConfigReader reads application properties and supports environment overrides for database keys.
- WaitUtils centralizes explicit wait operations.
- ScreenshotUtils captures browser evidence for failed tests.
- TestListener records TestNG pass/fail outcomes in Extent Reports and attaches failure screenshots when a WebDriver is available.
- DBUtils provides JDBC query, update and catalog synchronization helpers.

The listener and report manager are part of the framework, while GitHub Actions uploads Surefire reports as the CI artifact.

## API examples

The default API class uses REST Assured against the external Reqres sample API, with request/response POJOs and JSON schema validation. The REQRES_API_KEY environment variable is required. These tests demonstrate API automation concepts; they do not validate a GreenKart application API or service.

LoginViaAPITest is a separate example that sends an HTTP login request to the-internet.herokuapp.com and injects the returned session cookie into a browser. It is not included in the default suite.

## Database examples and current limitations

The database classes use a MySQL database separate from the public GreenKart application. They are intended as JDBC and cross-layer test exercises.

Important limitations before enabling these classes in a CI suite:

1. DBUtils now opens a connection per operation, closes JDBC resources with try-with-resources and returns detached row data rather than live ResultSet objects.
2. SQL errors are surfaced as IllegalStateException with the original SQLException attached as the cause.
3. The canonical database seed is src/test/resources/init.sql; the default UI/API workflow does not create a MySQL service.
4. DatabaseSyncTest leaves category null when the public UI does not expose category data.
5. DatabaseDrivenSearchTest now orders its selected rows by product_id, but those names are not guaranteed to exist in the live public UI.

The database classes are therefore excluded from the default 93-execution CI metric. A separate deterministic database suite should be established after connection handling, fixtures and test isolation are corrected.

## Validated CI path

The validated pipeline is .github/workflows/maven.yml:

1. Check out the repository.
2. Set up Temurin JDK 21 (Maven compiles against Java 17).
3. Run mvn clean test using testng.xml.
4. Upload target/surefire-reports even when tests fail.

The GitHub Actions workflow is the only CI path with linked full-suite execution evidence. The Jenkinsfile and GitLab configuration require separate correction and validation.