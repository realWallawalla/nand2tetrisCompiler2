package com.timonsarakinis.tokens;

import com.timonsarakinis.tokens.types.TokenType;

public class StringConstant implements Token {
    private String value;

    public StringConstant(String value) {
        this.value = value;
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.STRING_CONST;
    }

    @Override
    public String getValue() {
        return value;
    }
}
