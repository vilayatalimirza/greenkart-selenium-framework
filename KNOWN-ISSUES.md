# Known issues and validation notes

This file records observed failures and implementation risks. A passing run does not erase a prior failure.

## Historical CI observations

| Workflow run | Result | Evidence |
|---|---|---|
| [#17](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35285130645) | 93 tests, 1 failure, 0 errors, 0 skipped | `PlaceOrderTest.verifyCountryDropdownContainsCountries` timed out waiting for `//select[@style='width: 200px;']` |
| [#18](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions/runs/35330099292) | 93 tests, 0 failures, 0 errors, 0 skipped | Full Maven test job succeeded and Surefire reports were uploaded |

The latest successful run (#18) is a single execution snapshot: 93/93 passed (100% first-attempt pass rate for that run). It is not a long-term reliability statistic. Earlier workflow runs also include failures; the full run history is available in [GitHub Actions](https://github.com/vilayatalimirza/greenkart-selenium-framework/actions).

## Observed failure: country dropdown locator

**Test:** `PlaceOrderTest.verifyCountryDropdownContainsCountries`

**Observed in run #17:** Selenium waited 60 seconds for the element located by:

```java
//select[@style='width: 200px;']
```

The element was not found, resulting in a `TimeoutException`. The failure is consistent with a brittle locator that depends on an exact inline style attribute. Run #18 passed, but a later pass alone does not prove the locator has been made robust. Prefer a stable semantic locator (label, name, id, or a scoped selector) after inspecting the live DOM.

## Implementation risks found during repository review

### Shared JDBC connection

`DBUtils` stores a single static `Connection`, while `testng.xml` executes test classes in parallel. Parallel database tests can share or close the same connection unexpectedly. Use a connection-per-test/per-operation strategy or a properly scoped connection provider before depending on concurrent database execution.

### JDBC resource handling and error propagation

The current helper methods create statements and prepared statements without consistently closing them. Some query/update methods return `null` or `-1` after SQL errors, which can hide the original failure and cause less informative downstream assertions. Use try-with-resources and propagate meaningful exceptions.

### Database data assumptions

Some database tests assert fixed values for the seeded product `Brocolli` (price 60.00, category Vegetable). The GitHub Actions workflow seeds these values in a dedicated MySQL service. This validates the test database setup and JDBC checks, not synchronization with the real GreenKart backend.

### Catalog sync wording

`DatabaseSyncTest` previously printed that the local database was “100% aligned with Production,” although it only upserts products scraped from the public demo UI into a separately configured local test database. The portfolio-readiness branch corrects this wording, reports successful and failed upserts separately, and fails the test if any visible product cannot be synchronized. It still does not compare the entire production catalog or connect to the demo application's backend database.

### CI configuration drift

- The GitHub Actions workflow is the validated path and has a successful run linked in the README.
- The Jenkinsfile references `testng-customsuite.xml` and uses a Windows `bat` step. That suite file is not present in the repository tree, so the Jenkinsfile should not be advertised as a working pipeline without correction and a real run.
- The GitLab pipeline uses a separate Maven/Chrome setup and has not been validated by the GitHub Actions run evidence.

### Browser coverage

The driver factory contains Chrome and Firefox branches. The recorded successful GitHub Actions run uses Chrome on Ubuntu. Do not claim Firefox or cross-browser execution coverage until those runs are captured.

## Next engineering improvements

1. Replace the country dropdown's exact-style XPath with a stable locator and add a focused regression assertion.
2. Refactor JDBC connection, statement and result-set lifecycle for parallel safety.
3. Make database-dependent tests fail fast with actionable setup errors when the database is unavailable.
4. Correct the catalog sync log wording and report inserted/updated/failed row counts accurately.
5. Align the Jenkinsfile with the actual suite and agent OS, or remove it if Jenkins is not a supported execution path.
6. Run and capture separate Chrome and Firefox results if cross-browser coverage is a project goal.
