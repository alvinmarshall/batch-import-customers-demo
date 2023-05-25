package com.migmeninfo.cipservice.common;

public enum CustomerType {
    INDIVIDUAL("IND"),
    ORGANISATION("ORG"),
    FINANCIAL("FIN"),
    OTHER("OTHER");

    private final String name;

    CustomerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static CustomerType fromString(String text) {
        for (CustomerType type : CustomerType.values()) {
            if (type.name.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return CustomerType.OTHER;
    }
}
