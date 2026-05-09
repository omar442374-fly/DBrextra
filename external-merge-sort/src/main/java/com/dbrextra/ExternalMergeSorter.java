package com.dbrextra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class ExternalMergeSorter {

    private ExternalMergeSorter() {
    }

    public static List<Path> sortInputFiles(
            List<Path> inputFiles,
            Path sortedDirectory,
            Comparator<EmployeeRecord> comparator) throws IOException {
        List<Path> sortedFiles = new ArrayList<>();
        Files.createDirectories(sortedDirectory);

        for (int i = 0; i < inputFiles.size(); i++) {
            Path inputFile = inputFiles.get(i);
            List<EmployeeRecord> records = EmployeeFileManager.readAllRecords(inputFile);
            records.sort(comparator);
            Path outputFile = sortedDirectory.resolve(String.format("sorted_%02d.csv", i + 1));
            EmployeeFileManager.writeRecords(outputFile, records);
            sortedFiles.add(outputFile);
        }

        return sortedFiles;
    }

    public static void mergeSortedFiles(
            List<Path> sortedFiles,
            Path outputFile,
            Comparator<EmployeeRecord> comparator,
            int inputBufferSize) throws IOException {
        Files.createDirectories(outputFile.getParent());

        List<BufferedRecordReader> readers = new ArrayList<>();
        PriorityQueue<HeapItem> heap = new PriorityQueue<>((a, b) -> comparator.compare(a.record(), b.record()));

        try {
            for (int index = 0; index < sortedFiles.size(); index++) {
                BufferedRecordReader reader = new BufferedRecordReader(sortedFiles.get(index), inputBufferSize);
                readers.add(reader);
                EmployeeRecord record = reader.poll();
                if (record != null) {
                    heap.offer(new HeapItem(record, index));
                }
            }

            try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
                while (!heap.isEmpty()) {
                    HeapItem smallest = heap.poll();
                    writer.write(smallest.record().toCsvLine());
                    writer.newLine();

                    EmployeeRecord next = readers.get(smallest.readerIndex()).poll();
                    if (next != null) {
                        heap.offer(new HeapItem(next, smallest.readerIndex()));
                    }
                }
            }
        } finally {
            for (BufferedRecordReader reader : readers) {
                reader.close();
            }
        }
    }

    private record HeapItem(EmployeeRecord record, int readerIndex) {
    }

    private static final class BufferedRecordReader implements AutoCloseable {
        private final BufferedReader reader;
        private final int bufferSize;
        private final ArrayDeque<EmployeeRecord> buffer = new ArrayDeque<>();
        private boolean exhausted;

        private BufferedRecordReader(Path filePath, int bufferSize) throws IOException {
            if (bufferSize <= 0) {
                throw new IllegalArgumentException("inputBufferSize must be greater than 0");
            }
            this.reader = Files.newBufferedReader(filePath);
            this.bufferSize = bufferSize;
        }

        private EmployeeRecord poll() throws IOException {
            if (buffer.isEmpty() && !exhausted) {
                fillBuffer();
            }
            return buffer.pollFirst();
        }

        private void fillBuffer() throws IOException {
            while (buffer.size() < bufferSize) {
                String line = reader.readLine();
                if (line == null) {
                    exhausted = true;
                    break;
                }
                if (!line.isBlank()) {
                    buffer.addLast(EmployeeRecord.fromCsv(line));
                }
            }
        }

        @Override
        public void close() throws IOException {
            reader.close();
        }
    }
}
