package com.migmeninfo.cipservice.common;

public enum Gender {
    MALE("M"),
    FEMALE("F"),
    OTHER("O");
    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
