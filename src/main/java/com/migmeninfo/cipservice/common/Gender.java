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

    @Override
    public String toString() {
        return name;
    }

    public static Gender fromString(String text) {
        for (Gender gender : Gender.values()) {
            if (gender.name.equalsIgnoreCase(text)) {
                return gender;
            }
        }
        return Gender.OTHER;
    }
}
