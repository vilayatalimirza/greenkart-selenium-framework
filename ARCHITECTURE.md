# Architecture and execution flow

## High-level structure

    GitHub Actions
          |
          +--> Browser/API job
          |       |
          |       +--> Maven Surefire --> testng.xml
          |               |
          |               +--> 12 UI test classes
          |               |       |
          |               |       +--> Page objects --> Selenium --> GreenKart demo
          |               |
          |               +--> 1 API test class
          |                       |
          |                       +--> REST Assured --> Reqres sample API
          |
          +--> Database job
                  |
                  +--> MySQL 8 service + init.sql fixture
                  |
                  +--> Maven Surefire --> testng-db.xml
                          |
                          +--> DatabaseSmokeTest
                          +--> DatabaseCRUDTest

The two jobs are independent. The browser/API job does not start MySQL. The database job does not launch Chrome or call the public GreenKart website.

## Browser/API suite scope

Maven Surefire defaults to \`testng.xml\`. The suite lists 13 classes and executes UI classes in parallel (four threads):

- 12 UI classes exercise the public GreenKart demo.
- \`ProductApiTest\` exercises Reqres user endpoints through REST Assured. The class name is legacy and does not imply a GreenKart product API.

The recorded full-suite run count is 93 test executions. This figure does not include the database-only job or the three browser/database integration examples outside CI.

## Database suite scope

The database job uses \`testng-db.xml\`, a serial suite containing:

- \`DatabaseSmokeTest\`: verifies the Brocolli price and category in the disposable test database.
- \`DatabaseCRUDTest\`: inserts a uniquely named test product, updates it through the upsert helper, verifies price and category preservation, then deletes the test record.

The job starts MySQL 8 and initializes it from \`src/test/resources/init.sql\`. This is a separate test database, not the database backing the public GreenKart application.

The following classes remain outside both CI suites because they combine database work with the live demo site:

- \`DatabaseDrivenSearchTest\`
- \`DatabaseSyncTest\`
- \`UItoDBValidationTest\`

\`LoginViaAPITest\` is also outside both suites and targets a separate demo authentication website.

## WebDriver and test lifecycle

- \`DriverFactory\` creates Chrome or Firefox drivers based on the browser setting.
- WebDriver is stored in a \`ThreadLocal\`, allowing parallel TestNG classes to use separate browser instances.
- \`BaseTest\` creates the driver and explicit wait before each test method and quits the driver afterward.
- GitHub Actions sets the \`CI\` environment variable, enabling headless Chrome flags.

Only Chrome is represented by the linked successful browser/API CI runs. The Firefox branch in \`DriverFactory\` is not proof of validated Firefox coverage.

## Search behavior

\`GreenKartPage.searchProduct()\` clears the search field using keyboard events, enters the query and waits for the expected visible-result state. The expected state is either one or more matching product cards or an empty result list for a query with no match.

\`getVisibleProductNames()\` returns only currently visible product headings. It does not require at least one heading to exist, because a valid no-match query can remove product headings from the DOM. Page-load synchronization is kept separately in \`waitForPageLoad()\`.

Run #29 passed the full browser/API suite after the no-results and promo-flow changes. The valid promo test verified a total change from 200 to 180; see [KNOWN-ISSUES.md](KNOWN-ISSUES.md) for the prior failure history.

## Country selector

The country dropdown locator was changed from an XPath tied to an exact inline style attribute to a CSS selector for the \`select\` element. The updated selector passed in the latest completed browser/API runs; the historical failure remains in the issue log.

## Utilities and reporting

- \`ConfigReader\` reads application properties and supports environment overrides for database keys.
- \`WaitUtils\` centralizes explicit wait operations.
- \`ScreenshotUtils\` captures browser evidence for failed tests.
- \`TestListener\` records TestNG pass/fail outcomes in Extent Reports and attaches failure screenshots when a WebDriver is available.
- \`DBUtils\` provides parameterized JDBC query, update and product upsert helpers.

The JDBC helper opens a connection per operation, closes \`Connection\`, \`PreparedStatement\` and \`ResultSet\` resources with try-with-resources, and returns detached row data instead of exposing an open ResultSet. SQL failures are surfaced as an \`IllegalStateException\` with the original \`SQLException\` attached as the cause.

When the public UI does not provide a product category, the upsert helper preserves the existing database category using \`COALESCE\` rather than replacing it with null.

## API examples

The default API test class uses REST Assured against the external Reqres sample API, with request/response POJOs and JSON schema validation. The \`REQRES_API_KEY\` environment variable is required. These tests demonstrate API automation concepts; they do not validate a GreenKart service.

## CI workflow

The validated workflow is \`.github/workflows/maven.yml\` and contains two separate jobs.

### Browser/API job

1. Check out the repository.
2. Set up Temurin JDK 21 (Maven compiles against Java 17).
3. Run \`mvn clean test\` using \`testng.xml\`.
4. Upload \`target/surefire-reports/\` even when tests fail.

### Database job

1. Start MySQL 8 as a service container.
2. Check out the repository and set up Temurin JDK 21.
3. Initialize the disposable database from \`src/test/resources/init.sql\`.
4. Run \`mvn -Dtestng.suite=testng-db.xml clean test\`.
5. Upload database Surefire reports even when tests fail.

The Jenkinsfile and GitLab CI configuration are present but are not validated CI paths.