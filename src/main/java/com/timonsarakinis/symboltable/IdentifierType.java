package com.timonsarakinis.symboltable;

public enum IdentifierType {
    STATIC("static"), FIELD("fields"), ARG("arg"), VAR("var");

    private final String name;

    IdentifierType(String nodeName) {
        this.name = nodeName;
    }

    public String getName() {
        return name;
    }
}
