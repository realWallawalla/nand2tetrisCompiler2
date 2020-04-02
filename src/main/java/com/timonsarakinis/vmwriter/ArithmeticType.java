package com.timonsarakinis.vmwriter;

public enum ArithmeticType {
    SUB("-"), ADD("+"), NEG("!"), EQ("="), GT("&gt;"), LT("&lt;"), AND("&amp;"), OR("|"), NOT("!");

    private final String symbol;

    ArithmeticType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
