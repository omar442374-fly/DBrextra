package com.dbrextra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExternalMergeSorterTest {

    @TempDir
    Path tempDir;

    @Test
    void mergesSortedFilesInAscendingEmployeeIdOrder() throws IOException {
        Path inputDir = tempDir.resolve("input");
        Path sortedDir = tempDir.resolve("sorted");
        Path outputFile = tempDir.resolve("output").resolve("employees_sorted.csv");

        List<EmployeeRecord> fileOne = List.of(
                new EmployeeRecord(5, "Brown", "Ava", "HR", 45000),
                new EmployeeRecord(1, "Smith", "Liam", "IT", 50000));

        List<EmployeeRecord> fileTwo = List.of(
                new EmployeeRecord(4, "Davis", "Mia", "Sales", 52000),
                new EmployeeRecord(2, "Jones", "Noah", "Engineering", 78000),
                new EmployeeRecord(3, "Miller", "Emma", "Finance", 68000));

        Path input1 = inputDir.resolve("employees_01.csv");
        Path input2 = inputDir.resolve("employees_02.csv");
        EmployeeFileManager.writeRecords(input1, fileOne);
        EmployeeFileManager.writeRecords(input2, fileTwo);

        List<Path> sortedFiles = ExternalMergeSorter.sortInputFiles(
                List.of(input1, input2),
                sortedDir,
                Comparator.comparingInt(EmployeeRecord::employeeId));

        ExternalMergeSorter.mergeSortedFiles(
                sortedFiles,
                outputFile,
                Comparator.comparingInt(EmployeeRecord::employeeId),
                1);

        List<String> lines = Files.readAllLines(outputFile);
        assertEquals(5, lines.size());

        int previousId = Integer.MIN_VALUE;
        for (String line : lines) {
            EmployeeRecord record = EmployeeRecord.fromCsv(line);
            assertTrue(record.employeeId() >= previousId);
            previousId = record.employeeId();
        }
    }
}
