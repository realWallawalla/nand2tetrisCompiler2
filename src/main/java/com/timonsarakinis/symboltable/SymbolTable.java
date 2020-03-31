package com.timonsarakinis.symboltable;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void define(String name, String type, VariableType kind) {
        switch (kind) {
            case STATIC:
                classTable.put(name, new SymbolRow(name, type, kind, staticCounter));
                staticCounter++;
                break;
            case FIELD:
                classTable.put(name, new SymbolRow(name, type, kind, fieldCounter));
                fieldCounter++;
                break;
            case ARG:
                functionTable.put(name, new SymbolRow(name, type, kind, argCounter));
                argCounter++;
                break;
            case VAR:
                functionTable.put(name, new SymbolRow(name, type, kind, varCounter));
                varCounter++;
                break;
        }
    }

    @Override
    public int varCount(VariableType kind) {
        long varCount;
        if (kind == VariableType.FIELD || kind == VariableType.STATIC) {
            varCount = getCount(kind, classTable.values());
        } else {
            varCount = getCount(kind, functionTable.values());
        }
        return (int) varCount;
    }

    private long getCount(VariableType kind, Collection<SymbolRow> rows) {
        return rows.stream().filter(row -> row.getKind() == kind).count();
    }

    @Override
    public VariableType kindOf(String key) {
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
