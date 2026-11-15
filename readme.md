# GreenKart UI, API & Database Test Automation

A Java-based test automation project for the public [GreenKart demo application](https://rahulshettyacademy.com/seleniumPractise/). It combines Selenium UI checks, TestNG, REST Assured API tests, and JDBC/MySQL exercises in one Maven project.

The repository is intended as a practical QA engineering demonstration. It is **not** a production-grade application test environment: GreenKart is a public demo site, the API examples use an external sample API, and the database is a separately seeded local/CI test database.

## Latest recorded CI evidence

<details>
<summary>GitHub Actions run #18 · 18 September 2026</summary>

[Open workflow run #18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292)

| Result | Count |
|---|---:|
| Tests executed | 93 |
| Passed | 93 |
| Failed | 0 |
| Errors | 0 |
| Skipped | 0 |
| First-attempt pass rate | 100% |

This is the result of **one recorded workflow run**, not a long-term reliability guarantee or a claim that every future execution will pass. The run completed the Maven test job and uploaded Surefire reports.

The immediately preceding run, [#17](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35285130645), recorded 93 tests with one failure. The failure was `PlaceOrderTest.verifyCountryDropdownContainsCountries`: the test timed out locating a select element using the exact style XPath `//select[@style='width: 200px;']`. See [KNOWN-ISSUES.md](KNOWN-ISSUES.md).

</details>

Historical workflow runs are available in [GitHub Actions](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions). The workflow history contains both successful and failed runs; the latest success is not presented as proof of consistently stable execution.

## What is in the project

| Layer | Implemented examples |
|---|---|
| UI automation | Product search, product list, cart, quantity, promo code, checkout, country selection, order flow |
| Framework | Java 17 compilation target, Selenium WebDriver, TestNG, Maven, Page Object Model, ThreadLocal WebDriver, explicit waits |
| Test support | Configuration reader, test data utilities, screenshots, Log4j2 logging, Extent Reports listener |
| API testing | REST Assured GET/POST checks, status and payload assertions, POJO serialization/deserialization, JSON schema validation |
| Database exercises | JDBC utilities, parameterized queries, product data retrieval, UI-to-DB price comparison, UI catalog-to-database synchronization |
| CI | GitHub Actions with MySQL service and Surefire report artifact; GitLab CI configuration is also present |

The current TestNG suite references 13 test classes, including UI, API and database-related tests. The most recent successful run reported 93 total test executions. Counts can change as the suite changes; use the linked run and report artifact as the evidence for that snapshot.

## Architecture

- `src/main/java/com/vilayat/base`: WebDriver lifecycle and base test setup.
- `src/main/java/com/vilayat/pages`: page objects for GreenKart interactions.
- `src/main/java/com/vilayat/utils`: configuration, waits, screenshots, test data and JDBC helpers.
- `src/main/java/com/vilayat/listeners`: TestNG listener and Extent Reports integration.
- `src/test/java/com/vilayat/tests`: UI, API and database test classes.
- `src/test/java/com/vilayat/api`: REST API test base and request/response models.
- `src/main/resources`: application configuration and Log4j2 configuration.
- `testng.xml`: suite composition and parallel class execution.
- `pom.xml`: dependency and Maven Surefire configuration.
- `.github/workflows/maven.yml`: GitHub Actions pipeline.

See [ARCHITECTURE.md](ARCHITECTURE.md) for execution flow and design notes.

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
| CI runtime | GitHub Actions, Ubuntu, Temurin JDK 21, MySQL 8 |

The CI runtime uses JDK 21 while the project compiles against Java 17. This is intentional compatibility testing of the Java 17 target on a newer runtime.

## Run locally

### Prerequisites

- JDK 17 or newer
- Maven 3.9+
- Google Chrome for UI tests
- MySQL 8 if running database-dependent tests
- Network access to the public GreenKart demo and the sample API used by API tests

### Clone and run

```bash
git clone https://github.com/vilayatalimirza/greenkart-selenium-framework.git
cd greenkart-selenium-framework
mvn clean test
```

Maven Surefire is configured to execute `testng.xml`. The suite runs test classes in parallel (4 threads). Some tests require a working MySQL database and the configured external API. Configure the database connection using the `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables; the GitHub Actions workflow demonstrates the expected CI database setup.

The checked-in `config.properties` contains local defaults. Do not put personal or production credentials in that file. For database tests, create the expected `greenkart_db.products` table and seed data compatible with the assertions in the database tests.

### Reports

- TestNG/Surefire XML and text reports: `target/surefire-reports/`
- Extent report output: configured by the report manager in the project
- CI artifact: [Surefire reports from run #18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292/artifacts)

## Test documentation

The repository includes [GreenKart_TestingDOC_Enhanced.xlsx](GreenKart_TestingDOC_Enhanced.xlsx), a manual test documentation workbook. It is separate from the automated test execution report.

## Known limitations

- The public GreenKart application and external sample API are outside this repository's control. Their availability and data can change.
- The current CI evidence is Chromium-only. Firefox support exists in the driver factory, but a successful Firefox CI run is not documented here.
- The JDBC helper currently uses a shared static connection and does not consistently close statements/result sets. This is a concurrency and resource-management risk under the parallel TestNG suite.
- Some database tests assume fixed product data (for example, Brocolli at 60.00 and category Vegetable). The CI workflow seeds this test data; this is not evidence of a production database integration.
- The Jenkinsfile and GitLab pipeline are present but are not the validated CI path described above. See [KNOWN-ISSUES.md](KNOWN-ISSUES.md).

## Author

**Vilayat Ali Mirza**

- GitHub: [@vilayatalimirza](https://github.com/vilayatalimirza)
- LinkedIn: [Vilayat Ali Mirza](https://www.linkedin.com/in/vilayat-ali-mirza/)

---

*Metrics are tied to a specific workflow run and are not edited to hide failures. If a newer run changes the result, update the evidence and failure history rather than silently replacing it.*
