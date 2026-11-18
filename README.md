# Rosewood Payroll: Net Pay Service

A small service that works out a payslip (gross pay, tax and net pay) for an employee from their hours and hourly
rate. It was refactored from a single AI-written calculator. Its design decisions and the evidence behind them are in
[ANALYSIS.md](ANALYSIS.md).

## Requirements

- JDK 21
- Maven 3.9 or later

## Design at a glance

| Layer | Package | What it holds |
|---|---|---|
| Domain | `domain.model`, `domain.pay`, `domain.tax` | Payslip, Employee, Money rounding, gross pay rule, tax bands |
| Port | `port` | `EmployeeRepository`, the one interface (the real I/O boundary) |
| Adapter | `adapter.persistence` | `InMemoryEmployeeRepository`, which holds only the employees it is given |
| Application | `application` | `PayrollService`, which coordinates the steps and never swallows a failure |
| Validation | `validation` | `PayInputValidator`, which checks inputs before any lookup |
| Composition root | `Demo` | Wires the parts together, supplies sample staff, and reports failures |

Business rules:

- Gross pay: hours up to 40 at the hourly rate, then the extra hours at 1.5 times the rate.
- Tax: 5% below GHS 1,000; 15% from GHS 1,000 up to GHS 5,000; 25% from GHS 5,000. The rate applies to the whole gross
  amount (see the open question in `ANALYSIS.md`, section 5).
- Money is rounded to two decimals, half-up.
- Tax bands live in a table in `StandardTaxBands`. Adding a band is adding a row.

Input limits, all of which are assumptions to confirm with the business:

- Hours: 0 to 168 per pay period.
- Hourly rate: 0 to GHS 10,000.
- Employee id: must not be blank.

## Running it

Run the demo (prints sample payslips and the failure cases):

```bash
mvn compile exec:java -Dexec.mainClass=com.rosewood.payroll.Demo
```

Run the tests:

```bash
mvn test
```

Measure the tests:

```bash
mvn verify                                                  # tests + JaCoCo report: target/site/jacoco/index.html
mvn test-compile org.pitest:pitest-maven:mutationCoverage   # PIT report: target/pit-reports/index.html
```

## Tests

- `PayrollServiceTest` is a golden table of hand-worked payslips. Only the repository is mocked.
- `PayrollServiceIntegrationTest` runs the real wiring with no mocks.
- `GrossPayCalculatorTest`, `TaxCalculatorTest`, `FlatRateTaxBandTest` and `MoneyTest` check each rule on its own,
  including the exact boundaries (999.99, 1000.00, 4999.99, 5000.00).
- `TaxBandCoverageTest` and `NewTaxBandExtensionTest` check that the bands leave no gap and no overlap, and that a new
  band can be added without editing existing ones. `TaxBandAssertions` holds the shared check.
- `PayInputValidatorTest` and `InMemoryEmployeeRepositoryTest` cover the boundary cases of inputs and lookups.

Test data lives in test code only. `SampleStaff` provides the sample employees for the tests, and `Demo` has its own
copy for the demo.
