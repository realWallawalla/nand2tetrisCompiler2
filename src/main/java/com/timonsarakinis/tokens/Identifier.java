package com.timonsarakinis.tokens;

import com.timonsarakinis.tokens.types.TokenType;

public class Identifier implements Token {
    private String value;

    public Identifier(String value) {
        this.value = value;
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.IDENTIFIER;
    }

    @Override
    public String getValue() {
        return value;
    }
}
