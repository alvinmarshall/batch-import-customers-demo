package com.migmeninfo.cipservice.common;

public enum MaritalStatus {
    SINGLE("Single"),
    MARRIED("Married"),
    DIVORCED("Divorced"),
    OTHER("Other");
    private final String name;

    MaritalStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static MaritalStatus fromString(String text) {
        for (MaritalStatus status : MaritalStatus.values()) {
            if (status.name.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return MaritalStatus.OTHER;
    }
}
