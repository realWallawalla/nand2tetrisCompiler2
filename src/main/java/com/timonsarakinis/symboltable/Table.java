package com.timonsarakinis.symboltable;

public interface Table {
    void startSubRoutine();

    void define(String name, String type, String kind);

    int varCount(IdentifierType type);

    String kindOf(String key);

    String typeOf(String key);

    int indexOf(String key);

}
