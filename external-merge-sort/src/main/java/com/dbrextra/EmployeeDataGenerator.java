package com.dbrextra;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class EmployeeDataGenerator {

    private static final String[] FIRST_NAMES = {
            "Liam", "Olivia", "Noah", "Emma", "Elijah", "Ava", "Mason", "Sophia", "Lucas", "Mia"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Wilson", "Moore"
    };

    private static final String[] DEPARTMENTS = {
            "Engineering", "Finance", "HR", "Sales", "Operations", "Marketing", "IT", "R&D"
    };

    private EmployeeDataGenerator() {
    }

    public static List<Path> generateFiles(Path inputDirectory, int fileCount, int recordsPerFile, long seed)
            throws IOException {
        Random random = new Random(seed);
        List<Path> generatedFiles = new ArrayList<>();
        int nextEmployeeId = 100000;

        for (int fileIndex = 1; fileIndex <= fileCount; fileIndex++) {
            List<EmployeeRecord> records = new ArrayList<>(recordsPerFile);
            for (int i = 0; i < recordsPerFile; i++) {
                records.add(new EmployeeRecord(
                        nextEmployeeId++,
                        LAST_NAMES[random.nextInt(LAST_NAMES.length)],
                        FIRST_NAMES[random.nextInt(FIRST_NAMES.length)],
                        DEPARTMENTS[random.nextInt(DEPARTMENTS.length)],
                        30000 + random.nextDouble() * 120000));
            }
            java.util.Collections.shuffle(records, random);
            Path filePath = inputDirectory.resolve(String.format("employees_%02d.csv", fileIndex));
            EmployeeFileManager.writeRecords(filePath, records);
            generatedFiles.add(filePath);
        }

        return generatedFiles;
    }
}
