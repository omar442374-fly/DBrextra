package com.dbrextra;

import java.util.Locale;

public record EmployeeRecord(
        int employeeId,
        String lastName,
        String firstName,
        String department,
        double salary) {

    public static EmployeeRecord fromCsv(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid employee record: " + line);
        }
        return new EmployeeRecord(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                Double.parseDouble(parts[4].trim()));
    }

    public String toCsvLine() {
        return String.format(Locale.US, "%d,%s,%s,%s,%.2f",
                employeeId, lastName, firstName, department, salary);
    }
}
