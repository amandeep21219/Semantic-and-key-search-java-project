package com.example.PdfReader.enums;

public enum Enums {
    SEMANTIC("SEMANTIC");

    private final String value;

    Enums(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}