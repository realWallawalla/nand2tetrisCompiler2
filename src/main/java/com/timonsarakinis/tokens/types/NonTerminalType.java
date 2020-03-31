package com.timonsarakinis.tokens.types;

import com.timonsarakinis.tokens.NonTerminalToken;

public enum NonTerminalType implements NonTerminalToken {
    SUBROUTINE_DEC("subroutineDec"),
    CLASS_VAR_DEC("classVarDec"),
    PARAMETER_LIST("parameterList"),
    SUBROUTINE_BODY("subroutineBody"),
    VAR_DEC("varDec"),
    STATEMENTS("statements"),
    EXPRESSION_LIST("expressionList"),
    EXPRESSION("expression"),
    CLASS("class"),
    TERM("term");

    private final String nodeName;

    NonTerminalType(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }
}
