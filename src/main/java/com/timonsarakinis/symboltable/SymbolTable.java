package com.timonsarakinis.symboltable;


import com.timonsarakinis.tokens.types.KeywordType;
import com.timonsarakinis.vmwriter.VmSegmentType;

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
        if (kind.equals(VmSegmentType.STATIC.getSegment())) {
            classTable.put(name, new SymbolRow(name, type, kind, staticCounter));
            staticCounter++;
        } else if (kind.equals(KeywordType.FIELD.getValue())) {
            classTable.put(name, new SymbolRow(name, type, kind, fieldCounter));
            fieldCounter++;
        } else if (kind.equals(VmSegmentType.ARG.getSegment())) {
            functionTable.put(name, new SymbolRow(name, type, kind, argCounter));
            argCounter++;
        } else if (kind.equals(VmSegmentType.LOCAL.getSegment())) {
            functionTable.put(name, new SymbolRow(name, type, kind, varCounter));
            varCounter++;
        }
    }

    @Override
    public int varCount(IdentifierType kind) {
        long varCount;
        if (kind == IdentifierType.FIELD || kind == IdentifierType.STATIC) {
            varCount = getCount(kind.toString().toLowerCase(), classTable.values());
        } else {
            varCount = getCount(kind.toString().toLowerCase(), functionTable.values());
        }
        return (int) varCount;
    }

    private long getCount(String kind, Collection<SymbolRow> rows) {
        return rows.stream().filter(row -> row.getKind().equals(kind)).count();
    }

    @Override
    public String kindOf(String key) {
        SymbolRow row = getSymbolRow(key);
        String kind = "";
        if (row != null) {
            kind = row.getKind().equals("field") ? "this" : row.getKind();
        }
        return kind;
    }

    @Override
    public String typeOf(String key) {
        SymbolRow row = getSymbolRow(key);
        return row != null ? row.getType() : "";
    }

    @Override
    public int indexOf(String key) {
        SymbolRow row = getSymbolRow(key);
        return row != null ? row.getIndex()  : -1;
    }

    private SymbolRow getSymbolRow(String key) {
        return classTable.getOrDefault(key, functionTable.getOrDefault(key, null));
    }

    public int getLocalCount() {
        return functionTable.size();
    }

    public int getClassCount() {
        return classTable.size();
    }
}
