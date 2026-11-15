# Architecture and execution flow

## High-level structure

    GitHub Actions
          |
          +--> MySQL 8 service and seed step
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

The database test classes exist in the repository but are not currently listed in testng.xml. The MySQL service is started and seeded by the workflow, but the default suite does not execute the database test classes. The CI pass counts must therefore not be presented as JDBC test coverage.

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

The country dropdown currently uses an XPath tied to an exact inline style attribute. That locator caused a timeout in historical CI run #17 and remains a maintenance risk.

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

1. DBUtils uses a single static Connection. This is not safe for parallel database classes.
2. Query/update helpers do not consistently close Statements, PreparedStatements or ResultSets.
3. Some SQL failures are returned as null or -1 instead of preserving the original exception.
4. The workflow seed data and src/test/resources/init.sql contain different product values.
5. DatabaseSyncTest assigns the same "Vegetable" category to every product scraped from the UI, although the UI does not provide verified category data.
6. DatabaseDrivenSearchTest selects the first three database records without an explicit order or a guarantee that those names exist in the live UI.

The database classes are therefore excluded from the default 93-execution CI metric. A separate deterministic database suite should be established after connection handling, fixtures and test isolation are corrected.

## Validated CI path

The validated pipeline is .github/workflows/maven.yml:

1. Start MySQL 8 as a service container.
2. Check out the repository.
3. Set up Temurin JDK 21 (Maven compiles against Java 17).
4. Create and seed the test database.
5. Run mvn clean test using testng.xml.
6. Upload target/surefire-reports even when tests fail.

The GitHub Actions workflow is the only CI path with linked full-suite execution evidence. The Jenkinsfile and GitLab configuration require separate correction and validation.