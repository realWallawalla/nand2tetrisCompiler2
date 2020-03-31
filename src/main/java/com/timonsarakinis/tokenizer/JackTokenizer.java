package com.timonsarakinis.tokenizer;

import com.timonsarakinis.factory.FactoryProvider;
import com.timonsarakinis.factory.TokenFactory;
import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.TokenType;
import com.timonsarakinis.utils.TokenUtils;

import java.util.List;
import java.util.ListIterator;

import static java.util.Objects.requireNonNull;

public class JackTokenizer implements Tokenizer {
    /*Removes all comments and white space from the input stream and breaks it into Jack-language tokens,
     as specified by the Jack grammar */
    private ListIterator<String> iterator;
    private Token currentToken;

    public JackTokenizer(List<String> tokens) {
        this.iterator = tokens.listIterator();
    }

    public boolean hasMoreTokens() {
        return iterator.hasNext();
    }

    public void advance() {
        String token = iterator.next();
        String tokenType = TokenUtils.FindTokenType(token);
        TokenFactory tokenFactory = (TokenFactory) FactoryProvider.getFactory(Token.class.getSimpleName());
        this.currentToken = requireNonNull(tokenFactory).create(tokenType, token);
    }

    @Override
    public TokenType tokenType() {
        return currentToken.getTokenType();
    }

    @Override
    public Token getCurrentToken() {
        return currentToken;
    }
}
