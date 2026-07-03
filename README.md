# Private Cloud Storage

A Spring Boot lab project that implements a private cloud storage experience with user isolation, quota enforcement, a simple dashboard UI, and a JUnit/Allure test suite.

## Run the app

```bash
./mvnw spring-boot:run
```

Open http://localhost:8090/dashboard after the app starts.

## Run the tests

```bash
./mvnw test
```

## Allure report

```bash
./mvnw allure:report
```

Then open `target/site/allure-maven-plugin/index.html`.

## Testing method mapping

| Testing method  | Demonstrated by                                              |
| --------------- | ------------------------------------------------------------ |
| Content equals  | `StorageFlowTests.newUserGets50MbQuotaAndFormulaHolds`       |
| Contains        | `StorageFlowTests.folderListContainsUploadedFileName`        |
| Regex matched   | `StorageFlowTests.profileEndpointContractUsesRegexAndSchema` |
| Formula matched | `StorageFlowTests.newUserGets50MbQuotaAndFormulaHolds`       |
| Predicate       | `StorageFlowTests.usersCannotSeeEachOther`                   |
| Collection      | `StorageFlowTests.listOrderingAndNoDuplicates`               |
| Exception       | `StorageFlowTests.overQuotaUploadReturnsBadRequest`          |
| Tolerance       | `StorageFlowTests.usedBytesStayWithinTolerance`              |
| Schema/JSON     | `StorageFlowTests.profileEndpointContractUsesRegexAndSchema` |
| Delete account  | `StorageFlowTests.userCanDeleteTheirAccount`                 |
| Visual/snapshot | `StorageFlowTests.dashboardPageRendersStorageUi`             |

## Additional notes

- The application now exposes a self-delete endpoint at `POST /api/auth/delete`.
- The dashboard includes a delete-account button.
- Allure test artifacts include attached JSON payloads and rendered dashboard HTML.
- Use `-Dplaywright=true` to enable the Playwright UI smoke test.
