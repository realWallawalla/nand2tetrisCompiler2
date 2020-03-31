package com.timonsarakinis.factory;

import com.timonsarakinis.tokens.Token;

public class FactoryProvider {
    public static Factory getFactory(String choice) {
        if (Token.class.getSimpleName().equals(choice)) {
            return new TokenFactory();
        }
        return null;
    }
}
