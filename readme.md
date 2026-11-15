# GreenKart UI, API and Database Test Automation

A Java-based QA automation learning project for the public [GreenKart demo application](https://rahulshettyacademy.com/seleniumPractise/). The repository combines Selenium UI automation, TestNG, REST Assured examples against an external sample API, and JDBC/MySQL exercises.

This is a portfolio demonstration built around a public demo website and a separate test database. It is not a production test environment, and the local MySQL database is not the database behind the GreenKart application.

## Latest recorded CI evidence

The figures below are from individual GitHub Actions runs. They are run-specific results, not a long-term reliability estimate.

| Workflow run | Date (UTC) | Test executions | Passed | Failed | Errors | Skipped | First-attempt pass rate |
|---|---|---:|---:|---:|---:|---:|---:|
| [#21](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769436492) | 15 Nov 2026 | 93 | 93 | 0 | 0 | 0 | **100.0%** |
| [#20](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769427987) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |
| [#18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292) | 18 Sep 2026 | 93 | 93 | 0 | 0 | 0 | **100.0%** |
| [#17](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35285130645) | 17 Sep 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |

Run #20 failed in SearchEdgeCasesTest.verifyClearingSearchRestoresFullProductList. The test's product-list helper was counting hidden product elements, which could make a filtered list appear unchanged. A visibility-aware helper and result-state wait have since been added; run #23 and later runs will determine whether the issue is resolved. See [KNOWN-ISSUES.md](KNOWN-ISSUES.md).

Run #17 failed in PlaceOrderTest.verifyCountryDropdownContainsCountries while locating the country dropdown; run #18 passed. The exact-style XPath has since been replaced with a CSS selector for the select element; the new selector requires CI validation.

**Interpretation:** run #21 is the latest recorded clean run at 93/93 (100%). The immediately preceding run was 92/93 (98.9%). Both results are retained here so the successful run does not erase the failure history.

## What is implemented

| Area | Repository evidence |
|---|---|
| UI automation | Product search, product list, cart operations, quantity, promo code, checkout, country selection and order flow |
| Framework | Java 17 compiler target, Selenium WebDriver, TestNG, Maven, Page Object Model, ThreadLocal WebDriver, explicit waits |
| Test support | Configuration reader, test data, screenshots, Log4j2 logging, TestNG listener and Extent Reports |
| API examples | REST Assured GET/POST examples, response assertions, POJO mapping and JSON schema validation against Reqres |
| Database exercises | JDBC utilities, parameterized queries, product retrieval, CRUD examples, UI-to-database comparison and catalog upsert example |
| CI | GitHub Actions workflow with Chrome and Surefire report artifact |

### What the default CI suite actually runs

The Maven Surefire configuration points to **testng.xml**. That suite currently lists 13 test classes:

- **12 UI test classes** covering the GreenKart demo site.
- **1 API test class**, ProductApiTest, which uses the Reqres sample user API. Despite the legacy class name, it is not a GreenKart product API.

The five database test classes (DatabaseCRUDTest, DatabaseDrivenSearchTest, DatabaseSmokeTest, DatabaseSyncTest and UItoDBValidationTest) are present in the source tree but are **not listed in the default suite**. LoginViaAPITest is also outside the default suite and targets a separate demo authentication site.

Therefore, the 93 executions in runs #17, #18, #20 and #21 do **not** represent database test coverage or every test class in the repository. The GitHub Actions workflow validates only the configured UI and Reqres API suite. It does not start MySQL or execute the database classes.

## Architecture

- **src/main/java/com/vilayat/base**: WebDriver lifecycle and base test setup.
- **src/main/java/com/vilayat/pages**: Page objects for GreenKart interactions.
- **src/main/java/com/vilayat/utils**: Configuration, waits, screenshots, test data and JDBC helpers.
- **src/main/java/com/vilayat/listeners**: TestNG listener and Extent Reports integration.
- **src/test/java/com/vilayat/tests**: UI, API and database example classes.
- **src/test/java/com/vilayat/api**: REST Assured base and request/response models.
- **src/main/resources**: Application and Log4j2 configuration.
- **testng.xml**: Default suite composition and parallel class execution.
- **pom.xml**: Dependencies, Java compiler target and Maven Surefire configuration.
- **.github/workflows/maven.yml**: Validated GitHub Actions pipeline.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the execution flow and the distinction between default-suite coverage and optional database exercises.

## Technology stack

| Technology | Repository configuration |
|---|---|
| Java | Maven compiler release 17 |
| Selenium Java | 4.47.0 |
| TestNG | 7.11.0 |
| Maven Surefire | 3.2.5 |
| WebDriverManager | 5.9.2 |
| REST Assured | 5.4.0 |
| MySQL Connector/J | 8.4.0 |
| Extent Reports | 5.1.1 |
| Log4j2 | 2.23.1 |
| CI runtime | GitHub Actions, Ubuntu, Temurin JDK 21 |

The CI runtime uses JDK 21 while the Maven compiler targets Java 17.

## Run the default suite locally

### Prerequisites

- JDK 17 or newer
- Maven 3.9+
- Google Chrome
- Network access to the public GreenKart demo and Reqres API
- A Reqres API key, exposed as the REQRES_API_KEY environment variable

### PowerShell

    git clone https://github.com/vilayatalimirza/greenkart-selenium-framework.git
    cd greenkart-selenium-framework
    $env:REQRES_API_KEY = "<your-Reqres-API-key>"
    mvn clean test

The CI workflow supplies this value through a GitHub Actions secret. Do not commit API keys or passwords to the repository.

### Running the database examples

The database test classes are not part of the default testng.xml suite. They require a MySQL 8 database and the following environment variables:

    $env:DB_URL = "jdbc:mysql://127.0.0.1:3306/greenkart_db"
    $env:DB_USERNAME = "<your-db-user>"
    $env:DB_PASSWORD = "<your-db-password>"

The database examples should be run and validated separately against the disposable fixture. They are not part of the default CI run. Their results are not included in the CI figures above.

## Reports and test documentation

- Surefire XML and text reports: target/surefire-reports/
- CI run summary and downloadable Surefire artifact: [latest successful run #21](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769436492)
- Manual test documentation workbook: [GreenKart_TestingDOC_Enhanced.xlsx](GreenKart_TestingDOC_Enhanced.xlsx)

The workbook is manual test documentation and is separate from the automated execution report.

## Known limitations

- The public GreenKart demo and Reqres API are external dependencies whose availability and data can change.
- Only Chrome is covered by the linked successful CI run. The driver factory contains a Firefox branch, but no successful Firefox CI run is documented.
- The five database test classes are not included in the default TestNG suite. The default workflow does not start MySQL and should not be mistaken for database test coverage.
- DBUtils now opens a connection per operation, closes JDBC resources with try-with-resources, returns detached row data and preserves SQL exceptions as causes.
- The database fixture is provided in src/test/resources/init.sql for a separately configured disposable MySQL test database. See KNOWN-ISSUES.md before running the database exercises.
- DatabaseSyncTest assigns the category "Vegetable" to every scraped product; the public UI does not provide verified category data for each product.
- The Jenkinsfile and GitLab pipeline are present but are not validated CI paths. The Jenkinsfile references a suite file that is absent from the repository and uses a Windows batch step.
- The country dropdown locator and one search edge-case test have observed failure history. See KNOWN-ISSUES.md.

## Author

**Vilayat Ali Mirza**

- GitHub: [@vilayatalimirza](https://github.com/vilayatalimirza)
- LinkedIn: [Vilayat Ali Mirza](https://www.linkedin.com/in/vilayat-ali-mirza/)

---

*Metrics are tied to specific workflow runs. New failures should be recorded, not silently overwritten by a later passing run.*