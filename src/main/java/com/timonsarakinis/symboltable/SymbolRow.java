package com.timonsarakinis.symboltable;

public class SymbolRow {
    private String name;
    private String type;
    private String kind;
    private int index;

    public SymbolRow(String name, String type, String kind, int index) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }
}