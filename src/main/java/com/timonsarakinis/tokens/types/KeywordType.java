package com.timonsarakinis.tokens.types;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public enum KeywordType {
    CLASS("class"),
    METHOD("method"),
    FUNCTION("function"),
    CONSTRUCTOR("constructor"),
    INT("int"),
    BOOLEAN("boolean"),
    CHAR("char"),
    VOID("void"),
    VAR("var"),
    STATIC("static"),
    FIELD("field"),
    LET("let"),
    DO("do"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    RETURN("return"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    THIS("this");

    private String value;

    KeywordType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<KeywordType> getKeywordConstants() {
        return Collections.unmodifiableList(Lists.newArrayList(TRUE, FALSE, NULL, THIS));
    }
}
