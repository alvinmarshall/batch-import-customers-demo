package com.migmeninfo.cipservice.common;

public enum CustomerType {
    INDIVIDUAL("IND"),
    ORGANISATION("ORG");

    private final String name;

    CustomerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
