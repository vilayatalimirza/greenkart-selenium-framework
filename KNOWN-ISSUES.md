# Known issues and validation notes

This file retains observed failures and implementation risks. A later passing run does not erase a previous failure.

## CI execution history

| Workflow run | Date (UTC) | Executions | Passed | Failed | Errors | Skipped | First-attempt pass rate |
|---|---|---:|---:|---:|---:|---:|---:|
| [#21](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769436492) | 15 Nov 2026 | 93 | 93 | 0 | 0 | 0 | 100.0% |
| [#20](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769427987) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |
| [#18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292) | 18 Sep 2026 | 93 | 93 | 0 | 0 | 0 | 100.0% |
| [#17](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35285130645) | 17 Sep 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |

These are separate, individual runs of the default TestNG suite. Run #21 is the latest clean run, not a long-term reliability statistic. Each run covers 13 classes from testng.xml (12 UI classes and one Reqres API class); the database test classes are excluded.

## Observed failure: search edge-case test

**Run:** #20  
**Test:** SearchEdgeCasesTest.verifyClearingSearchRestoresFullProductList  
**Observed result:** 92 passed, 1 failed, 0 errors, 0 skipped.

The assertion failed before the clear operation because the test expected the partial search to reduce the visible product list, but the observed count was not smaller than the original list.

Run #21 passed all 93 executions. The relevant search test and shared search helper were unchanged between runs #20 and #21, so this is an observed intermittent failure, not a verified fix.

Potential contributing issues in the current implementation:

- GreenKartPage.searchProduct waits for the searched text to appear in a product-name element. For a query such as "Bro", that text may already exist in the unfiltered catalogue, so the wait can complete before the filtered list has updated.
- The helper catches TimeoutException and logs a warning rather than propagating a synchronization failure.
- GreenKartPage.getVisibleProductNames returns an empty list after a visibility timeout, which can make a page-load problem look like a valid zero-result search.

The exact root cause has not yet been isolated. Improve synchronization around the result list and retain a regression run before marking this issue resolved.

## Historical failure: country dropdown locator

**Run:** #17  
**Test:** PlaceOrderTest.verifyCountryDropdownContainsCountries

The test timed out locating the country dropdown with this exact-style XPath:

    //select[@style='width: 200px;']

Run #18 passed, but the same locator is still present in GreenKartPage. The later pass does not remove the locator risk. Prefer a stable id, name, label or scoped selector after checking the live DOM.

## Default suite scope: database tests are not executed

The default testng.xml suite includes 12 UI test classes and ProductApiTest. It does not include:

- DatabaseCRUDTest
- DatabaseDrivenSearchTest
- DatabaseSmokeTest
- DatabaseSyncTest
- UItoDBValidationTest
- LoginViaAPITest (separate external authentication demo)

Therefore, the 93-test CI results do not validate the JDBC or UI-to-database test classes. The workflow currently starts and seeds MySQL, but these database classes are not run by the default Maven suite.

## Database implementation risks

### Shared JDBC connection and resource handling

DBUtils stores one static Connection while the TestNG suite runs classes in parallel. The query helpers return ResultSet objects without closing the underlying Statement or PreparedStatement. Update methods also do not consistently close their PreparedStatement.

This creates concurrency and resource-leak risks if multiple database classes are executed together. Some SQL errors are caught and converted into null or -1 return values, which can hide the originating SQL failure.

### Fixture drift

The workflow seed and src/test/resources/init.sql do not contain the same values.

- The workflow seeds Cucumber at 30.00, Brocolli at 60.00 and Beetroot at 40.00.
- init.sql seeds Brocolli at 120.00, Cauliflower at 60.00, Cucumber at 48.00 and Beetroot at 32.00.

Database tests should use one canonical fixture so local and CI execution do not silently test different baselines.

### Catalog sync category assumption

DatabaseSyncTest currently passes "Vegetable" for every product found on the public UI. The page does not provide verified category data for every product, so this value should not be represented as a scraped or authoritative category.

The test now counts successful and failed upserts separately and fails if a product cannot be synchronized. It writes only to the configured local test database. It does not connect to the GreenKart application's backend or prove that a production catalog is aligned.

### Database-driven search data

DatabaseDrivenSearchTest selects three rows using LIMIT 3 without a deterministic ORDER BY and assumes the returned product names can be searched on the public UI. Once catalog sync has inserted additional names, these rows may no longer be predictable. Use isolated test data and deterministic ordering before including this class in a suite.

## CI configuration and browser coverage

- GitHub Actions is the only pipeline with linked full-suite execution evidence.
- The Jenkinsfile references testng-customsuite.xml, which is absent from the repository, and uses a Windows bat step. The Jenkins path is not validated.
- The GitLab CI configuration is present but has no linked successful execution evidence.
- The linked successful run is Chrome-only. Firefox support in DriverFactory does not establish Firefox coverage.
- CI logs emit a Selenium warning that no CDP implementation matches browser version 157. The warning did not fail run #21, but browser/CDP compatibility should be reviewed if CDP-specific functionality is added.

## Next engineering improvements

1. Make search synchronization wait for the filtered result state rather than text that may already exist in the original catalogue.
2. Replace the country dropdown's exact-style XPath with a stable locator.
3. Refactor DBUtils to use scoped connections and try-with-resources; preserve SQL exceptions rather than returning null or -1.
4. Align the workflow seed and init.sql, and isolate database test data.
5. Remove the hard-coded "Vegetable" category from the catalog sync unless category data is actually available.
6. Create a separate deterministic database suite before adding JDBC tests to the default parallel run.
7. Correct or remove the Jenkinsfile and validate the GitLab pipeline before listing either as supported CI.