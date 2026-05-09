package com.dbrextra;

import java.util.Comparator;

public enum SortCriterion {
    EMPLOYEE_ID,
    LAST_NAME;

    public Comparator<EmployeeRecord> comparator() {
        return switch (this) {
            case EMPLOYEE_ID -> Comparator.comparingInt(EmployeeRecord::employeeId);
            case LAST_NAME -> Comparator
                    .comparing(EmployeeRecord::lastName)
                    .thenComparing(EmployeeRecord::firstName)
                    .thenComparingInt(EmployeeRecord::employeeId);
        };
    }
}
