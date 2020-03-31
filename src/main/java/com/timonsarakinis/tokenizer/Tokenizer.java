package com.timonsarakinis.tokenizer;

import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.TokenType;

public interface Tokenizer {
    TokenType tokenType();

    Token getCurrentToken();

    boolean hasMoreTokens();

    void advance();

}
