# GreenKart UI, API and Database Test Automation

A Java QA automation project for the public [GreenKart demo application](https://rahulshettyacademy.com/seleniumPractise/). It combines Selenium UI automation, TestNG, REST Assured examples against the external Reqres sample API, and JDBC/MySQL exercises.

This is a portfolio project built around a public demo website and a separate test database. It is not a production test environment. The local MySQL database is not the database behind the GreenKart application.

## Latest main-branch CI run

The latest completed default-branch workflow is [run #33, 16 November 2026](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43850589978), on commit `7010621`. Both CI jobs passed:

| Suite | Executions | Passed | Failed | Errors | Skipped |
|---|---:|---:|---:|---:|---:|
| Browser and external Reqres API | 93 | 93 | 0 | 0 | 0 |
| Isolated MySQL/JDBC | 2 | 2 | 0 | 0 | 0 |

These are separate suites and should not be represented as one combined test total. The JDBC job uses a disposable MySQL 8 service, not the database behind GreenKart. The browser/API job includes the Reqres sample API test, not a GreenKart backend API test.

## CI evidence

The browser/API and database suites run in separate GitHub Actions jobs. Their counts must not be combined into one test total.

### Browser and API suite

This suite runs \`testng.xml\`: 12 UI test classes and one Reqres API test class. The following are individual run results, not a long-term reliability guarantee.

| Workflow run | Date (UTC) | Executions | Passed | Failed | Errors | Skipped | First-attempt pass rate |
|---|---|---:|---:|---:|---:|---:|---:|
| [#29](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43840579572) | 16 Nov 2026 | 93 | 93 | 0 | 0 | 0 | **100.0%** |
| [#28](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43839648334) | 16 Nov 2026 | 93 | 93 | 0 | 0 | 0 | **100.0%** |
| [#27](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43838482850) | 16 Nov 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |
| [#26](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837521394) | 16 Nov 2026 | 93 | 90 | 3 | 0 | 0 | **96.8%** |
| [#25](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837208899) | 16 Nov 2026 | 93 | 90 | 3 | 0 | 0 | **96.8%** |
| [#24](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43773126320) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |
| [#23](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43772949743) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |
| [#22](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43771739197) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |
| [#21](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769436492) | 15 Nov 2026 | 93 | 93 | 0 | 0 | 0 | **100.0%** |
| [#20](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769427987) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |
| [#18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292) | 18 Sep 2026 | 93 | 93 | 0 | 0 | 0 | **100.0%** |
| [#17](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35285130645) | 17 Sep 2026 | 93 | 92 | 1 | 0 | 0 | **98.9%** |

Runs #25 and #26 recorded three failures in no-results search cases. Run #27 confirms those checks now pass after removing the incorrect DOM-presence wait. Run #27's single failure was `PromoCodeTest.verifyTotalAmountDecreasesAfterValidPromo`: the total remained 200 before and after the promo attempt. The helper previously swallowed a 60-second response timeout; the latest revision waits for an explicit promo result and for the discounted total to change. Run #28/#29 validate the promo-flow correction. See [KNOWN-ISSUES.md](KNOWN-ISSUES.md).

The search clear-and-restore case passed in runs #25 and #26 after the keyboard-based reset change. The country dropdown selector was changed from an exact inline-style XPath to a CSS selector; it did not appear among the failures in those two runs.

### Isolated JDBC suite

The separate \`testng-db.xml\` suite runs two database-only tests: \`DatabaseSmokeTest\` and \`DatabaseCRUDTest\`.

| Workflow run | Date (UTC) | Executions | Passed | Failed | First-attempt pass rate |
|---|---|---:|---:|---:|---:|
| [#29](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43840579572) | 16 Nov 2026 | 2 | 2 | 0 | **100.0%** |
| [#28](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43839648334) | 16 Nov 2026 | 2 | 2 | 0 | **100.0%** |
| [#27](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43838482850) | 16 Nov 2026 | 2 | 2 | 0 | **100.0%** |
| [#26](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837521394) | 16 Nov 2026 | 2 | 2 | 0 | **100.0%** |
| [#25](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837208899) | 16 Nov 2026 | 2 | 1 | 1 | **50.0%** |

Run #25 exposed a fixture mismatch: the database seed had Brocolli at 120.00 while the smoke test expected 60.00. The canonical seed in \`src/test/resources/init.sql\` was corrected, and the database job passed 2/2 in runs #26 and #27. These are small, run-specific samples, not a broad reliability claim.

Run #29 is the latest completed browser/API run: 93/93 passed (100.0%). The promo-total assertion passed with 200 before and 180 after. Run #29's separate database job also passed 2/2. This is one clean execution snapshot, not a long-term reliability statistic.

## What is implemented

| Area | Repository evidence |
|---|---|
| UI automation | Product search, product list, cart operations, quantity, promo code, checkout, country selection and order flow |
| Framework | Java 17 compiler target, Selenium WebDriver, TestNG, Maven, Page Object Model, ThreadLocal WebDriver, explicit waits |
| Test support | Configuration reader, test data, screenshots, Log4j2 logging, TestNG listener and Extent Reports |
| API examples | REST Assured GET/POST examples, response assertions, POJO mapping and JSON schema validation against Reqres |
| JDBC | Parameterized queries and updates, detached result data, operation-scoped connections, CRUD and product upsert examples |
| CI | Separate GitHub Actions jobs for browser/API and MySQL/JDBC validation |

### Test scope

**Browser/API suite:** \`testng.xml\` contains 13 classes: 12 UI classes against the public GreenKart demo and \`ProductApiTest\`, a legacy-named class that exercises Reqres user endpoints. It does not test a GreenKart backend API.

**Database-only suite:** \`testng-db.xml\` contains \`DatabaseSmokeTest\` and \`DatabaseCRUDTest\`, and runs against a disposable MySQL 8 service initialized from \`src/test/resources/init.sql\`.

Three browser/database integration examples remain outside the CI suites because they combine JDBC with the public demo website: \`DatabaseDrivenSearchTest\`, \`DatabaseSyncTest\` and \`UItoDBValidationTest\`. \`LoginViaAPITest\` is also excluded; it targets a separate demo authentication website.

## Architecture

- \`src/main/java/com/vilayat/base\`: WebDriver lifecycle and base test setup.
- \`src/main/java/com/vilayat/pages\`: page objects for GreenKart interactions.
- \`src/main/java/com/vilayat/utils\`: configuration, waits, screenshots, test data and JDBC helpers.
- \`src/main/java/com/vilayat/listeners\`: TestNG listener and Extent Reports integration.
- \`src/test/java/com/vilayat/tests\`: UI, API and database example classes.
- \`src/test/java/com/vilayat/api\`: REST Assured base and request/response models.
- \`testng.xml\`: default browser/API suite.
- \`testng-db.xml\`: isolated database suite.
- \`.github/workflows/maven.yml\`: separate UI/API and database CI jobs.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the execution flow.

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
| CI runtime | GitHub Actions, Ubuntu, Temurin JDK 21; MySQL 8 in the database job |

The CI runtime uses JDK 21 while the Maven compiler targets Java 17.

## Run locally

### Browser/API suite

Prerequisites: JDK 17 or newer, Maven 3.9+, Chrome, network access to GreenKart and Reqres, and a Reqres API key.

PowerShell:

    git clone https://github.com/vilayatalimirza/greenkart-selenium-framework.git
    cd greenkart-selenium-framework
    $env:REQRES_API_KEY = "<your-Reqres-API-key>"
    mvn clean test

### Database suite

Start a disposable MySQL 8 database and configure:

    $env:DB_URL = "jdbc:mysql://127.0.0.1:3306/greenkart_db"
    $env:DB_USERNAME = "<your-db-user>"
    $env:DB_PASSWORD = "<your-db-password>"

Initialize the schema and seed data:

    mysql -h 127.0.0.1 -u<your-db-user> -p greenkart_db < src/test/resources/init.sql

Run the isolated suite:

    mvn -Dtestng.suite=testng-db.xml clean test

The database suite is serial and only includes the database-only smoke and CRUD tests. The three browser/database integration examples are not part of this command.

## Reports and test documentation

- Browser/API Surefire XML and text reports: \`target/surefire-reports/\`
- Database Surefire reports: \`target/surefire-reports/\` from the database CI job
- Workflow runs and downloadable artifacts: [GitHub Actions](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions)
- Manual test documentation workbook: [GreenKart_TestingDOC_Enhanced.xlsx](GreenKart_TestingDOC_Enhanced.xlsx)

The workbook is manual test documentation and is separate from automated execution reports.

## Known limitations

- The public GreenKart demo and Reqres API are external dependencies whose availability and data can change.
- Only Chrome is covered by the linked CI runs. Firefox support exists in \`DriverFactory\`, but no successful Firefox CI run is documented.
- Three browser/database integration examples remain outside CI and depend on both local MySQL data and the live public demo site.
- The JDBC helper is resource-scoped, but the separate database suite currently validates only two database-only scenarios.
- The Jenkinsfile references a suite file that is absent from the repository and uses a Windows batch step. The Jenkins path is not validated.
- The GitLab CI configuration is present but has no linked successful execution evidence.

## Author

**Vilayat Ali Mirza**

- GitHub: [@vilayatalimirza](https://github.com/vilayatalimirza)
- LinkedIn: [Vilayat Ali Mirza](https://www.linkedin.com/in/vilayat-ali-mirza/)

---

*Metrics are tied to specific workflow runs. New failures should be recorded, not silently overwritten by a later passing run.*