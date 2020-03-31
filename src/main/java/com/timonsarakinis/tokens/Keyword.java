package com.timonsarakinis.tokens;

import com.timonsarakinis.tokens.types.TokenType;

public class Keyword implements Token {
    private String value;

    public Keyword(String value) {
        this.value = value;
    }

    @Override
    public TokenType getTokenType() {
        return TokenType.KEYWORD;
    }

    @Override
    public String getValue() {
        return value;
    }
}
