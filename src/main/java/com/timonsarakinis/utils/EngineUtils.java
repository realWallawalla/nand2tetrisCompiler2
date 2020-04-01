package com.timonsarakinis.utils;

import static com.timonsarakinis.tokens.types.KeywordType.*;

public class EngineUtils {
    public static final String validIdentifier = "^[A-Za-z_]\\w*$";

    public static boolean isType(String token) {
        return token.equals(INT.getValue())
                || token.equals(CHAR.getValue())
                || token.equals(BOOLEAN.getValue())
                || token.matches(validIdentifier);
    }

    public static boolean isIdentifier(String token) {
        return token.matches(validIdentifier);
    }
}
