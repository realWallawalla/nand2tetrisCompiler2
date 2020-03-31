package com.timonsarakinis.tokens;

import com.timonsarakinis.tokens.types.TokenType;

public class IntegerConstant implements Token {
    private String value;

    public IntegerConstant(String value) {
        this.value = value;
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.INT_CONST;
    }

    @Override
    public String getValue() {
        return value;
    }
}
