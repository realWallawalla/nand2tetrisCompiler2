package com.timonsarakinis.engine;

import java.util.HashMap;
import java.util.Map;

public enum ArithmaticType {

    MULTIPLY("Math.multiply"),
    SUB("sub"),
    ADD("add"),
    DIVIDE("Math.divide"),
    AMPERSAND("and"),
    VERTICAL_BAR("or"),
    LESS_THAN("lt"),
    GREATER_THAN("gt"),
    EQUALS("eq"),
    NOT("not"),
    NEG("neg");

    public static Map <String, ArithmaticType> mappings = new HashMap<>();

    static {
        mappings.put("*", MULTIPLY);
        mappings.put("-", SUB);
        mappings.put("+", ADD);
        mappings.put("/", DIVIDE);
        mappings.put("&amp;", AMPERSAND);
        mappings.put("|", VERTICAL_BAR);
        mappings.put("&lt;", LESS_THAN);
        mappings.put("&gt;", GREATER_THAN);
        mappings.put("=", EQUALS);
        mappings.put("~", NOT);
    }

    private final String vmValue;

    ArithmaticType(String vmValue) {
        this.vmValue = vmValue;
    }

    public String getVmValue() {
        return vmValue;
    }
}
