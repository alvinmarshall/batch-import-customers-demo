package com.migmeninfo.cipservice.common;

public enum MaritalStatus {
    SINGLE("single"),
    MARRIED("married"),
    DIVORCED("divorced"),
    OTHER("other");
    private final String name;

    MaritalStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
