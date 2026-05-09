# DBrextra

Java implementation of the **External Merge Sort for Large Employee Records** project.

## Project layout

- `External Merge Sort Project.pdf` – original assignment.
- `external-merge-sort/` – Maven Java project implementation.

## How to run

```bash
cd /home/runner/work/DBrextra/DBrextra/external-merge-sort
mvn test
mvn exec:java
```

Optional sort criterion argument:

```bash
mvn exec:java -Dexec.args="last-name"
```

Generated output is written under:

- `data/input/` (16 files × 1000 records)
- `data/sorted/` (individually sorted files)
- `data/output/employees_sorted.csv` (final merged output)

## Report

See `external-merge-sort/REPORT.md`.
