package com.timonsarakinis.factory;

import com.timonsarakinis.tokens.*;
import com.timonsarakinis.tokens.types.TokenType;

public class TokenFactory implements Factory<Token>{
    @Override
    public Token create(String tokenType, String value) {
        Token token = null;
        switch (TokenType.valueOf(tokenType.toUpperCase())) {
           case SYMBOL:
               token = new Symbol(value);
               break;
            case KEYWORD:
                token = new Keyword(value);
                break;
            case INT_CONST:
                token = new IntegerConstant(value);
                break;
            case IDENTIFIER:
                token = new Identifier(value);
                break;
            case STRING_CONST:
                token = new StringConstant(value.replaceAll("\"", ""));
                break;
       }
        return token;
    }
}
