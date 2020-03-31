package com.timonsarakinis.factory;

import com.timonsarakinis.tokens.types.TokenType;

public interface Factory<T> {
    T create(String tokenType, String value);
}
