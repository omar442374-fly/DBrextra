package com.dbrextra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class EmployeeFileManager {

    private EmployeeFileManager() {
    }

    public static List<EmployeeRecord> readAllRecords(Path filePath) throws IOException {
        List<EmployeeRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    records.add(EmployeeRecord.fromCsv(line));
                }
            }
        }
        return records;
    }

    public static void writeRecords(Path filePath, List<EmployeeRecord> records) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (EmployeeRecord record : records) {
                writer.write(record.toCsvLine());
                writer.newLine();
            }
        }
    }
}
