package com.dbrextra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public final class ExternalMergeSortApp {

    private static final int FILE_COUNT = 16;
    private static final int RECORDS_PER_FILE = 1000;
    private static final int MERGE_INPUT_BUFFER_SIZE = 128;

    private ExternalMergeSortApp() {
    }

    public static void main(String[] args) throws IOException {
        SortCriterion criterion = parseSortCriterion(args);
        Comparator<EmployeeRecord> comparator = criterion.comparator();

        Path dataRoot = Path.of("data");
        Path inputDirectory = dataRoot.resolve("input");
        Path sortedDirectory = dataRoot.resolve("sorted");
        Path outputFile = dataRoot.resolve("output").resolve("employees_sorted.csv");

        clearDirectory(inputDirectory);
        clearDirectory(sortedDirectory);
        Files.createDirectories(outputFile.getParent());

        long generationStart = System.nanoTime();
        List<Path> generatedFiles = EmployeeDataGenerator.generateFiles(inputDirectory, FILE_COUNT, RECORDS_PER_FILE, 42L);
        long generationEnd = System.nanoTime();

        long sortStart = System.nanoTime();
        List<Path> sortedFiles = ExternalMergeSorter.sortInputFiles(generatedFiles, sortedDirectory, comparator);
        long sortEnd = System.nanoTime();

        long mergeStart = System.nanoTime();
        ExternalMergeSorter.mergeSortedFiles(sortedFiles, outputFile, comparator, MERGE_INPUT_BUFFER_SIZE);
        long mergeEnd = System.nanoTime();

        System.out.println("External Merge Sort completed.");
        System.out.println("Sort criterion: " + criterion);
        System.out.println("Generated files: " + generatedFiles.size());
        System.out.println("Total records: " + (FILE_COUNT * RECORDS_PER_FILE));
        System.out.println("Output file: " + outputFile.toAbsolutePath());
        System.out.printf("Data generation time: %.3f ms%n", nanosToMillis(generationEnd - generationStart));
        System.out.printf("In-memory sorting time: %.3f ms%n", nanosToMillis(sortEnd - sortStart));
        System.out.printf("Multi-way merge time: %.3f ms%n", nanosToMillis(mergeEnd - mergeStart));
        System.out.printf("Total processing time: %.3f ms%n",
                nanosToMillis((generationEnd - generationStart) + (sortEnd - sortStart) + (mergeEnd - mergeStart)));
    }

    private static SortCriterion parseSortCriterion(String[] args) {
        if (args.length == 0) {
            return SortCriterion.EMPLOYEE_ID;
        }

        String arg = args[0].trim().toLowerCase();
        return switch (arg) {
            case "employee-id", "id", "--sort=id" -> SortCriterion.EMPLOYEE_ID;
            case "last-name", "name", "--sort=last-name" -> SortCriterion.LAST_NAME;
            default -> throw new IllegalArgumentException("Unsupported sort criterion: " + args[0]);
        };
    }

    private static void clearDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            return;
        }

        try (var stream = Files.list(directory)) {
            for (Path path : stream.toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static double nanosToMillis(long nanos) {
        return nanos / 1_000_000.0;
    }
}
