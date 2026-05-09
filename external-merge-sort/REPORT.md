# External Merge Sort Project Report

## 1) Algorithm Selection Rationale and Implementation Details

This project uses **External Merge Sort** because it is appropriate for datasets that are large relative to memory availability. The implementation follows two phases:

1. **Run generation / per-file sorting**: each input file is loaded independently, sorted in memory, and written as a sorted run.
2. **Multi-way merge**: sorted runs are merged into one final sorted output using a priority queue.

A bounded input buffer per file is used during merge, so not all run data must be loaded at once.

## 2) Data Structure Design Choices

Employee records are represented by `EmployeeRecord` with fields:

- `employeeId` (int)
- `lastName` (String)
- `firstName` (String)
- `department` (String)
- `salary` (double)

Records are stored in CSV format and parsed/written via `EmployeeFileManager`.

## 3) Random Data Generation and File Handling

`EmployeeDataGenerator` creates **16 files**, each containing **1000 records**, for **16000 total records**.

- IDs are unique integers.
- Names/departments are sampled from fixed arrays.
- Salary values are random doubles in a realistic range.
- Each generated file is shuffled to avoid pre-sorted input.

File handling is split into dedicated read/write helpers and sorting/merging logic.

## 4) Time Complexity Analysis

Let:

- `N` = total records (16000)
- `k` = number of files/runs (16)
- `m` = records per file (1000)

### Per-file in-memory sorting
Each file sort: `O(m log m)`.
Across all files: `k * O(m log m)`.

### Multi-way merge
Priority queue of size `k` with one output operation per record:
`O(N log k)`.

### Overall
`O(k * m log m + N log k)`.

## 5) Results Discussion and Evaluation

The implementation demonstrates the expected behavior of External Merge Sort:

- Correctly sorts per-file chunks.
- Correctly merges all sorted chunks into one globally sorted output.
- Keeps merge memory usage bounded through fixed-size per-run buffers.

For the assignment scale (16000 records), execution is fast while preserving the external-sorting structure required for larger datasets.

## 6) Functional Notes

- Default criterion: `EMPLOYEE_ID` ascending.
- Optional criterion: `LAST_NAME` ascending.
- Runtime metrics are printed for:
  - data generation
  - per-file sorting
  - multi-way merge
  - total processing
