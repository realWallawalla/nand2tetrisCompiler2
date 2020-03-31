package com.timonsarakinis.tokens;

import com.timonsarakinis.tokens.types.TokenType;

public interface Token {

    TokenType getTokenType();

    String getValue();
}
