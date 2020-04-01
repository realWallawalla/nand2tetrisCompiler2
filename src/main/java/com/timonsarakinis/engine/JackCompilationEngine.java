package com.timonsarakinis.engine;

import com.timonsarakinis.tokenizer.Tokenizer;
import com.timonsarakinis.tokens.NonTerminalToken;
import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.*;
import com.timonsarakinis.utils.EngineUtils;
import com.timonsarakinis.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import static com.timonsarakinis.tokens.types.KeywordType.*;
import static com.timonsarakinis.tokens.types.NonTerminalType.*;
import static com.timonsarakinis.tokens.types.SymbolType.*;
import static com.timonsarakinis.utils.EngineUtils.*;
import static com.timonsarakinis.utils.TokenUtils.prepareNonTerminalForOutPut;
import static com.timonsarakinis.utils.TokenUtils.prepareTerminalForOutPut;
import static org.apache.commons.lang3.EnumUtils.getEnumIgnoreCase;
import static org.apache.commons.lang3.EnumUtils.isValidEnumIgnoreCase;

public class JackCompilationEngine implements Engine {

    private Tokenizer tokenizer;
    private final String fileName;

    public JackCompilationEngine(Tokenizer tokenizer, String fileName) {
        this.tokenizer = tokenizer;
        this.fileName = fileName;
    }

    @Override
    public void compile() {
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            //according to contract file has to start with class.
            compileClass();
        }
    }

    private void compileClass() {
        writeNonTerminalToFile(NonTerminalType.CLASS, true);
        eatAndAdvance(KeywordType.CLASS.getValue());
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        eatAndAdvance(OPEN_BRACE.getCharacter());

        while (EngineUtils.isCurrentTokenClassVar(getCurrentTokenValue())) {
            compileClassVarDeclaration();
        }

        while (EngineUtils.isCurrentTokenSubroutine(getCurrentTokenValue())) {
            compileSubroutineDeclaration();
        }
        eatAndAdvance(CLOSE_BRACE.getCharacter());
        writeNonTerminalToFile(NonTerminalType.CLASS, false);
    }

    private void compileClassVarDeclaration() {
        writeNonTerminalToFile(CLASS_VAR_DEC, true);
        eatAndAdvance(isCurrentTokenEqualTo(FIELD.getValue()) || isCurrentTokenEqualTo(STATIC.getValue()));

        eatAndAdvance(isType(getCurrentTokenValue()));
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));

        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        }
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(CLASS_VAR_DEC, false);
    }

    private void compileSubroutineDeclaration() {
        writeNonTerminalToFile(SUBROUTINE_DEC, true);
        eatAndAdvance(EngineUtils.isCurrentTokenSubroutine(getCurrentTokenValue()));
        eatAndAdvance(isType(getCurrentTokenValue()) || isCurrentTokenEqualTo(VOID.getValue()));
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));

        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        compileParameterList();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());

        compileSubroutineBody();
        writeNonTerminalToFile(SUBROUTINE_DEC, false);
    }

    private void compileParameterList() {
        writeNonTerminalToFile(PARAMETER_LIST, true);
        eatAndAdvance(isType(getCurrentTokenValue()));
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));

        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            eatAndAdvance(isType(getCurrentTokenValue()));
            eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        }

        writeNonTerminalToFile(PARAMETER_LIST, false);
    }

    private void compileSubroutineBody() {
        writeNonTerminalToFile(SUBROUTINE_BODY, true);
        eatAndAdvance(OPEN_BRACE.getCharacter());
        while (isCurrentTokenEqualTo(VAR.getValue())) {
            compileVarDeclaration();
        }

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
        writeNonTerminalToFile(SUBROUTINE_BODY, false);
    }

    private void compileVarDeclaration() {
        writeNonTerminalToFile(VAR_DEC, true);
        eatAndAdvance(VAR.getValue());
        eatAndAdvance(isType(getCurrentTokenValue()));
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        }
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(VAR_DEC, false);
    }

    private void compileStatements() {
        switch (getEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            case IF:
                compileIf();
                break;
            case WHILE:
                compileWhile();
                break;
            case DO:
                compileDo();
                break;
            case LET:
                compileLet();
                break;
            case RETURN:
                compileReturn();
                break;
        }
    }

    private void compileIf() {
        writeNonTerminalToFile(StatementType.IF, true);
        eatAndAdvance(KeywordType.IF.getValue());
        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        compileExpression();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
        if (isCurrentTokenEqualTo(ELSE.getValue())) {
            compileElse();
        }
        writeNonTerminalToFile(StatementType.IF, false);
    }

    private void compileElse() {
        eatAndAdvance(ELSE.getValue());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
    }

    private void compileWhile() {
        writeNonTerminalToFile(StatementType.WHILE, true);
        eatAndAdvance(KeywordType.WHILE.getValue());
        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        compileExpression();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());
        eatAndAdvance(OPEN_BRACE.getCharacter());

        writeNonTerminalToFile(STATEMENTS, true);
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }
        writeNonTerminalToFile(STATEMENTS, false);

        eatAndAdvance(CLOSE_BRACE.getCharacter());
        writeNonTerminalToFile(StatementType.WHILE, false);
    }

    private void compileDo() {
        writeNonTerminalToFile(StatementType.DO, true);
        eatAndAdvance(KeywordType.DO.getValue());
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        compileSubroutineCall();
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(StatementType.DO, false);
    }

    private void compileSubroutineCall() {
        if (getCurrentTokenValue().contains(DOT.getCharacter())) {
            eatAndAdvance(DOT.getCharacter());
            eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        }
        eatAndAdvance(OPEN_PARENTHESIS.getCharacter());
        compileExpressionList();
        eatAndAdvance(CLOSE_PARENTHESIS.getCharacter());
    }

    private void compileLet() {
        writeNonTerminalToFile(StatementType.LET, true);
        eatAndAdvance(KeywordType.LET.getValue());
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        if (isCurrentTokenEqualTo(OPEN_BRACKET.getCharacter())) {
            eatAndAdvance(OPEN_BRACKET.getCharacter());
            compileExpression();
            eatAndAdvance(CLOSE_BRACKET.getCharacter());
        }
        eatAndAdvance(EQUALS.getCharacter());
        compileExpression();
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(StatementType.LET, false);
    }

    private void compileReturn() {
        writeNonTerminalToFile(StatementType.RETURN, true);
        eatAndAdvance(RETURN.getValue());
        if (!getCurrentTokenValue().equals(SEMICOLON.getCharacter())) {
            compileExpression();
        }
        eatAndAdvance(SEMICOLON.getCharacter());
        writeNonTerminalToFile(StatementType.RETURN, false);
    }

    private void compileExpressionList() {
        writeNonTerminalToFile(EXPRESSION_LIST, true);
        if (!isCurrentTokenEqualTo(CLOSE_PARENTHESIS.getCharacter())) {
            compileExpression();
        }
        writeNonTerminalToFile(EXPRESSION_LIST, false);
    }

    private void compileExpression() {
        writeNonTerminalToFile(EXPRESSION, true);
        compileTerm();
        writeNonTerminalToFile(EXPRESSION, false);

        if (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            eatAndAdvance(COMMA.getCharacter());
            compileExpression();
        }

        eatAndAdvance(SEMICOLON.getCharacter());
    }

    private void compileTerm() {
        writeNonTerminalToFile(TERM, true);
        eatAndAdvance(isTermConstant(tokenizer.getCurrentToken()));
        if (isIdentifier(getCurrentTokenValue())) {
            lookAhead();
        } else if (isCurrentTokenEqualTo(OPEN_PARENTHESIS.getCharacter())) {
            eatAndAdvance(true);
            compileExpression();
            eatAndAdvance(isCurrentTokenEqualTo(CLOSE_PARENTHESIS.getCharacter()));
        } else if (isCurrentTokenEqualTo(HIPHON.getCharacter()) || isCurrentTokenEqualTo(TILDE.getCharacter())) {
            eatAndAdvance(true);
            compileTerm();
        }
        writeNonTerminalToFile(TERM, false);

        if (isOp()) {
            eatAndAdvance(true);
            compileTerm();
        }
    }

    private void lookAhead() {
        Token identifier = tokenizer.getCurrentToken();
        advance();
        if (isCurrentTokenEqualTo(OPEN_BRACKET.getCharacter())) {
            writeToFile(prepareTerminalForOutPut(identifier));
            eatAndAdvance(OPEN_BRACKET.getCharacter());
            compileExpression();
            eatAndAdvance(CLOSE_BRACKET.getCharacter());
        } else if (isCurrentTokenEqualTo(DOT.getCharacter())) {
            writeToFile(prepareTerminalForOutPut(identifier));
            compileSubroutineCall();
        } else {
            writeToFile(prepareTerminalForOutPut(identifier));
        }
    }

    private boolean isOp() {
        return SymbolType.getOperators().stream().anyMatch(symbolType -> isCurrentTokenEqualTo(symbolType.getCharacter()));
    }

    private void eatAndAdvance(String value) {
        Token currentToken = tokenizer.getCurrentToken();
        if (isCurrentTokenEqualTo(value)) {
            writeToFile(prepareTerminalForOutPut(currentToken));
            advance();
        } else {
            printError(currentToken);
        }
    }

    private void eatAndAdvance(boolean eat) {
        Token currentToken = tokenizer.getCurrentToken();
        if (eat) {
            writeToFile(prepareTerminalForOutPut(currentToken));
            advance();
        } else {
            printError(currentToken);
        }
    }

    private void advance() {
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        }
    }

    private void writeNonTerminalToFile(NonTerminalToken token, boolean open) {
        writeToFile(prepareNonTerminalForOutPut(token, open));
    }

    private void writeToFile(byte[] xml) {
        IOUtils.writeToFile(xml, fileName);
    }

    private void printError(Token currentToken) {
        System.out.printf("did not match with: %s continue \n", currentToken.getValue());
    }
    private boolean isCurrentTokenEqualTo(String value) {
        return StringUtils.equals(getCurrentTokenValue(), value);
    }

    private String getCurrentTokenValue() {
        return tokenizer.getCurrentToken().getValue();
    }
}