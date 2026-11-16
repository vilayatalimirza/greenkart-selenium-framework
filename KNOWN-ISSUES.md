# Known issues and validation notes

This file records observed failures, fixes and remaining limitations. A later passing run does not erase an earlier failure.

## Browser/API CI execution history

Each row is one execution of the default \`testng.xml\` suite.

| Workflow run | Date (UTC) | Executions | Passed | Failed | Errors | Skipped | First-attempt pass rate |
|---|---|---:|---:|---:|---:|---:|---:|
| [#26](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837521394) | 16 Nov 2026 | 93 | 90 | 3 | 0 | 0 | 96.8% |
| [#25](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837208899) | 16 Nov 2026 | 93 | 90 | 3 | 0 | 0 | 96.8% |
| [#24](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43773126320) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |
| [#23](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43772949743) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |
| [#22](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43771739197) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |
| [#21](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769436492) | 15 Nov 2026 | 93 | 93 | 0 | 0 | 0 | 100.0% |
| [#20](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43769427987) | 15 Nov 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |
| [#18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292) | 18 Sep 2026 | 93 | 93 | 0 | 0 | 0 | 100.0% |
| [#17](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35285130645) | 17 Sep 2026 | 93 | 92 | 1 | 0 | 0 | 98.9% |

Run #21 is the latest completed clean browser/API run at 93/93. Runs #25 and #26 each recorded 90/93 after three no-results search checks failed. The latest revision removes the incorrect DOM-presence wait; run #27 is validating that change.

## Search edge cases

### Clear-and-restore behavior

- Run #20: \`SearchEdgeCasesTest.verifyClearingSearchRestoresFullProductList\` failed because the list did not appear to narrow before clearing.
- Run #22: the same assertion failed again.
- Runs #23 and #24: the test reached the clear step but timed out while waiting for the full product list to return.
- Runs #25 and #26: the clear-and-restore test passed after the field reset was changed to select-all/backspace keyboard events.

### No-results behavior

Runs #25 and #26 failed these three tests:

- \`verifyNonExistingProductShowsNoResults\`
- \`verifyNumericSearchShowsNoResults\`
- \`verifySpecialCharactersDoNotCrashSearch\`

The stack trace pointed to \`getVisibleProductNames()\`. The helper was waiting for at least one product heading to exist. For a no-match query, the demo removes those heading elements from the DOM, so waiting for a heading was the wrong condition.

The latest revision removes that wait. The helper returns an empty list when no product heading exists, while \`searchProduct()\` retains its own explicit wait for the expected filtered result state. Run #27 is the first full-suite validation of this correction.

## Historical country dropdown failure

**Run:** #17  
**Test:** \`PlaceOrderTest.verifyCountryDropdownContainsCountries\`

The test timed out locating the country dropdown with this exact-style XPath:

    //select[@style='width: 200px;']

The locator has since been replaced by \`By.cssSelector("select")\`. Runs #25 and #26 completed without a country-dropdown failure. The historical failure remains recorded.

## Database CI execution history

The isolated \`testng-db.xml\` suite runs two database-only tests.

| Workflow run | Date (UTC) | Executions | Passed | Failed | Result |
|---|---|---:|---:|---:|---|
| [#27](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43838482850) | 16 Nov 2026 | 2 | 2 | 0 | Passed |
| [#26](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837521394) | 16 Nov 2026 | 2 | 2 | 0 | Passed |
| [#25](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/43837208899) | 16 Nov 2026 | 2 | 1 | 1 | Failed |

Run #25 exposed fixture drift: \`init.sql\` seeded Brocolli at 120.00 while \`DatabaseSmokeTest\` expected 60.00. The fixture was corrected to Cucumber 30.00, Brocolli 60.00 and Beetroot 40.00. Runs #26 and #27 then passed both database-only tests.

The CRUD test now verifies a null-category upsert preserves the existing category while updating the product price. It uses a unique test product and deletes it in a finally block.

## JDBC implementation

The previous \`DBUtils\` implementation used one static shared connection, returned live ResultSet objects and did not consistently close statements. It also converted some SQL failures to null or -1 return values.

The current implementation:

- Opens one connection per operation.
- Closes connections, prepared statements and result sets with try-with-resources.
- Returns detached row data instead of a live ResultSet.
- Surfaces SQL failures as an \`IllegalStateException\` with the original SQLException attached.
- Preserves an existing product category when a catalog upsert receives a null category.

The database CI job currently covers only \`DatabaseSmokeTest\` and \`DatabaseCRUDTest\`. It does not claim validation for every JDBC example in the repository.

## Database examples outside CI

These browser/database integration examples remain outside the CI suites:

- \`DatabaseDrivenSearchTest\`: depends on database-provided product names also existing in the live demo catalog.
- \`DatabaseSyncTest\`: scrapes the live demo and writes product data to the separate local test database.
- \`UItoDBValidationTest\`: compares a live demo price to a separately maintained local database value.

They require isolated fixtures and explicit handling of public-site data changes before being suitable for repeatable CI.

## CI configuration and browser coverage

- The browser/API and database suites are separate jobs in \`.github/workflows/maven.yml\`.
- GitHub Actions is the only pipeline with linked execution evidence.
- The Jenkinsfile references \`testng-customsuite.xml\`, which is absent from the repository, and uses a Windows \`bat\` step. The Jenkins path is not validated.
- The GitLab CI configuration is present but has no linked successful execution evidence.
- Browser CI evidence is Chrome-only. Firefox support in \`DriverFactory\` does not establish Firefox coverage.
- CI logs emit a Selenium warning that no CDP implementation matches browser version 157. It has not failed the recorded test jobs; review it if CDP-specific features are introduced.

## Next engineering improvements

1. Confirm the no-results helper change with a completed browser/API CI run.
2. Add deterministic fixtures before validating the remaining three browser/database integration examples.
3. Correct or remove the Jenkinsfile and validate the GitLab pipeline before listing either as supported CI.