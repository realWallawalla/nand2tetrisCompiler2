package com.timonsarakinis.tokens.types;

import com.timonsarakinis.tokens.NonTerminalToken;

public enum StatementType implements NonTerminalToken {
    IF("ifStatement"),
    WHILE("whileStatement"),
    DO("doStatement"),
    LET("letStatement"),
    RETURN("returnStatement");

    private final String node;

    StatementType(String node) {
        this.node = node;
    }

    @Override
    public String getNodeName() {
        return node;
    }
}
