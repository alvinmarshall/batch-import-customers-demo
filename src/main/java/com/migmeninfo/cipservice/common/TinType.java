package com.migmeninfo.cipservice.common;

public enum TinType {
    ITIN("ITIN"),
    SSN("SSN"),
    FOREIGN("EIN"),
    UNKNOWN("UNKNOWN");
    private final String name;

    TinType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
