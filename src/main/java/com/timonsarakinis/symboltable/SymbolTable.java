package com.timonsarakinis.symboltable;


import com.timonsarakinis.tokens.types.KeywordType;

import java.util.Collection;
import java.util.HashMap;

public class SymbolTable implements Table {
    private HashMap<String, SymbolRow> classTable;
    private HashMap<String, SymbolRow> functionTable;
    private int fieldCounter;
    private int staticCounter;
    private int varCounter;
    private int argCounter;

    public SymbolTable() {
        classTable = new HashMap<>();
        functionTable = new HashMap<>();
    }

    @Override
    public void startSubRoutine() {
        functionTable.clear();
        argCounter = 0;
        varCounter = 0;
    }

    @Override
    public void define(String name, String type, String kind) {
        if (kind.equals(KeywordType.STATIC.getValue())) {
            classTable.put(name, new SymbolRow(name, type, kind, staticCounter));
            staticCounter++;
        } else if (kind.equals(KeywordType.FIELD.getValue())) {
            classTable.put(name, new SymbolRow(name, type, kind, fieldCounter));
            fieldCounter++;
        } else if (kind.equals("arg")) {
            functionTable.put(name, new SymbolRow(name, type, kind, argCounter));
            argCounter++;
        } else if (kind.equals(KeywordType.VAR.getValue())) {
            functionTable.put(name, new SymbolRow(name, type, kind, varCounter));
            varCounter++;
        }
    }

    @Override
    public int varCount(IdentifierType kind) {
        long varCount;
        if (kind == IdentifierType.FIELD || kind == IdentifierType.STATIC) {
            varCount = getCount(kind, classTable.values());
        } else {
            varCount = getCount(kind, functionTable.values());
        }
        return (int) varCount;
    }

    private long getCount(IdentifierType kind, Collection<SymbolRow> rows) {
        return rows.stream().filter(row -> row.getKind().equals(kind)).count();
    }

    @Override
    public String kindOf(String key) {
        SymbolRow row = getSymbolRow(key);
        return row != null ? row.getKind() : null;
    }

    @Override
    public String typeOf(String key) {
        SymbolRow row = getSymbolRow(key);
        return row != null ? row.getType() : null;
    }

    @Override
    public int indexOf(String key) {
        SymbolRow row = getSymbolRow(key);
        return row != null ? row.getIndex() : 0;
    }

    private SymbolRow getSymbolRow(String key) {
        return classTable.getOrDefault(key, functionTable.getOrDefault(key, null));
    }
}
