package com.migmeninfo.cipservice.common;

public enum DocumentType {
    PHOTOGRAPH("display_picture"),
    IDENTITY_DOCUMENT("identification_documents"),
    INCORPORATION_DOCUMENT("incorporation_document"),
    OTHER("other");
    private final String name;

    DocumentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static DocumentType fromString(String text) {
        for (DocumentType type : DocumentType.values()) {
            if (type.name.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return DocumentType.OTHER;
    }
}
