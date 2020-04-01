package com.timonsarakinis.utils;

import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.TokenType;
import org.apache.commons.lang3.StringUtils;

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

    public static boolean isTermConstant(Token token) {
        return token.getTokenType() == TokenType.INT_CONST
                || token.getTokenType() == TokenType.STRING_CONST
                || StringUtils.equals(token.getValue(), THIS.getValue())
                || StringUtils.equals(token.getValue(), NULL.getValue())
                || StringUtils.equals(token.getValue(), TRUE.getValue())
                || StringUtils.equals(token.getValue(), FALSE.getValue());
    }

    public static boolean isCurrentTokenSubroutine(String token) {
        return StringUtils.equals(token, CONSTRUCTOR.getValue())
                || StringUtils.equals(token, FUNCTION.getValue())
                || StringUtils.equals(token, METHOD.getValue());
    }

    public static boolean isCurrentTokenClassVar(String token) {
        return StringUtils.equals(token, FIELD.getValue()) || StringUtils.equals(token, STATIC.getValue());
    }
}
