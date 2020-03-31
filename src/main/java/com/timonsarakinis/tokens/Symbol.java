package com.timonsarakinis.tokens;

import com.timonsarakinis.tokens.types.TokenType;

public class Symbol implements Token {
    private String value;

    public Symbol(String value) {
        this.value = value;
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.SYMBOL;
    }

    @Override
    public String getValue() {
        return value;
    }
}
