package com.timonsarakinis.tokens.types;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

public enum SymbolType {
    OPEN_BRACE("{"),
    CLOSE_BRACE("}"),
    OPEN_PARENTHESIS("("),
    CLOSE_PARENTHESIS(")"),
    OPEN_BRACKET("["),
    CLOSE_BRACKET("]"),
    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    MULTIPLY("*"),
    SUB("-"),
    ADD("+"),
    DIVIDE("/"),
    AMPERSAND("&amp;"),
    VERTICAL_BAR("|"),
    LESS_THAN("&lt;"),
    GREATER_THAN("&gt;"),
    EQUALS("="),
    TILDE("~");

    private final String character;

    SymbolType(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public static List<String> getCharacters() {
        return Arrays.stream(SymbolType.values()).map(SymbolType::getCharacter).collect(Collectors.toList());
    }

    public static List<SymbolType> getOperators() {
        return Collections.unmodifiableList(Lists.newArrayList(ADD, SUB, MULTIPLY, DIVIDE, AMPERSAND, VERTICAL_BAR, LESS_THAN, GREATER_THAN, EQUALS));
    }
}