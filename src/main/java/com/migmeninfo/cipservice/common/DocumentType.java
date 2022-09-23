package com.migmeninfo.cipservice.common;

public enum DocumentType {
    PHOTOGRAPH("display_picture"),
    IDENTITY_DOCUMENT("identification_documents");
    private final String name;

    DocumentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
