package com.timonsarakinis.symboltable;

public interface Table {
    void startSubRoutine();

    void define(String name, String type, VariableType kind);

    int varCount(VariableType type);

    VariableType kindOf(String key);

    String typeOf(String key);

    int indexOf(String key);

}
