package com.migmeninfo.cipservice.common;

public enum AccountType {
    SAVINGS("Savings"), CURRENT("Current"), OTHER("Other");
    private final String name;

    AccountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static AccountType fromString(String text) {
        for (AccountType type : AccountType.values()) {
            if (type.name.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return AccountType.OTHER;
    }
}
