package com.timonsarakinis.engine;

import com.timonsarakinis.symboltable.IdentifierType;
import com.timonsarakinis.symboltable.SymbolTable;
import com.timonsarakinis.tokenizer.Tokenizer;
import com.timonsarakinis.tokens.Token;
import com.timonsarakinis.tokens.types.KeywordType;
import com.timonsarakinis.tokens.types.StatementType;
import com.timonsarakinis.tokens.types.SymbolType;
import com.timonsarakinis.tokens.types.TokenType;
import com.timonsarakinis.vmwriter.JackVmWriter;
import com.timonsarakinis.vmwriter.VmWriter;
import org.apache.commons.lang3.StringUtils;

import static com.timonsarakinis.tokens.types.KeywordType.THIS;
import static com.timonsarakinis.tokens.types.KeywordType.*;
import static com.timonsarakinis.tokens.types.SymbolType.*;
import static com.timonsarakinis.utils.EngineUtils.*;
import static com.timonsarakinis.vmwriter.VmSegmentType.*;
import static org.apache.commons.lang3.EnumUtils.getEnumIgnoreCase;
import static org.apache.commons.lang3.EnumUtils.isValidEnumIgnoreCase;

public class JackCompilationEngine implements Engine {

    private Tokenizer tokenizer;
    private final String fileName;
    private SymbolTable symbolTable;
    private VmWriter vmWriter;
    private String className;
    private int nArgs;
    private int whileCounter;
    private int ifCounter;
    private String methodKind;

    public JackCompilationEngine(Tokenizer tokenizer, String fileName) {
        this.tokenizer = tokenizer;
        this.fileName = fileName;
    }

    @Override
    public void compile() {
        if (tokenizer.hasMoreTokens()) {
            //according to contract file has to start with class.
            symbolTable = new SymbolTable();
            vmWriter = new JackVmWriter(fileName);
            compileClass();
        }
    }

    private void compileClass() {
        tokenizer.advance();
        requireSymbol(CLASS.getValue());
        className = getCurrentTokenValue();
        eatAndAdvance(isIdentifier(className));
        requireSymbol(OPEN_BRACE.getCharacter());

        while (isCurrentTokenClassVar(getCurrentTokenValue())) {
            compileClassVarDeclaration();
        }

        while (isCurrentTokenSubroutine(getCurrentTokenValue())) {
            compileSubroutineDeclaration();
        }
        requireSymbol(CLOSE_BRACE.getCharacter());
    }

    private void compileClassVarDeclaration() {
        Token kind = tokenizer.getCurrentToken();
        advance();

        Token type = tokenizer.getCurrentToken();
        eatAndAdvance(isType(getCurrentTokenValue()));

        Token name = tokenizer.getCurrentToken();
        eatAndAdvance(isIdentifier(getCurrentTokenValue()));

        symbolTable.define(name.getValue(), type.getValue(), kind.getValue());
        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            advance();
            symbolTable.define(getCurrentTokenValue(), type.getValue(), kind.getValue());
            eatAndAdvance(isIdentifier(getCurrentTokenValue()));
        }
        requireSymbol(SEMICOLON.getCharacter());
    }

    private void compileSubroutineDeclaration() {
        symbolTable.startSubRoutine();
        methodKind = getCurrentTokenValue();
        boolean isMethod = StringUtils.equals(methodKind, METHOD.getValue());
        eatAndAdvance(isCurrentTokenSubroutine(methodKind));

        eatAndAdvance(isType(getCurrentTokenValue()) || isCurrentTokenEqualTo(VOID.getValue()));

        String functionName = getCurrentTokenValue();
        eatAndAdvance(isIdentifier(functionName));

        requireSymbol(OPEN_PARENTHESIS.getCharacter());
        if (isMethod) {
            symbolTable.incrementArgCounter();
        }
        if (!isCurrentTokenEqualTo(CLOSE_PARENTHESIS.getCharacter())) {
            compileParameterList();
        }
        requireSymbol(CLOSE_PARENTHESIS.getCharacter());
        requireSymbol(OPEN_BRACE.getCharacter());

        while (isCurrentTokenEqualTo(VAR.getValue())) {
            compileVarDeclaration();
        }

        vmWriter.writeFunction(getFunctionCall(functionName), symbolTable.varCount(IdentifierType.LOCAL));

       if (isMethod) {
            vmWriter.writePush(ARG.getSegment(), 0); //arg 0 is this in method
            vmWriter.writePop(POINTER.getSegment(), 0); //ancor this base address
        } else if (StringUtils.equals(methodKind, CONSTRUCTOR.getValue())) {
            vmWriter.writePush(CONST.getSegment(), symbolTable.varCount(IdentifierType.FIELD));
            vmWriter.writeCall("Memory.alloc", 1); //allocate memory for object. os function. return base address.
            vmWriter.writePop(POINTER.getSegment(), 0); //anchor base address to this
        }
        compileSubroutineBody();
    }

    private String getFunctionCall(String functionName) {
        return className + "." + functionName;
    }

    private void compileParameterList() {
        Token type = tokenizer.getCurrentToken();
        eatAndAdvance(isType(type.getValue()));

        Token name = tokenizer.getCurrentToken();
        eatAndAdvance(isIdentifier(name.getValue()));

        symbolTable.define(name.getValue(), type.getValue(), ARG.getSegment());

        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            advance();
            Token nextType = tokenizer.getCurrentToken();
            advance();
            Token nextName = tokenizer.getCurrentToken();
            advance();
            symbolTable.define(nextName.getValue(), nextType.getValue(), ARG.getSegment());
        }
    }

    private void compileSubroutineBody() {
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }

        requireSymbol(CLOSE_BRACE.getCharacter());
    }

    private void compileVarDeclaration() {
        requireSymbol(VAR.getValue());
        String type = getCurrentTokenValue();
        eatAndAdvance(isType(type));

        String name = getCurrentTokenValue();
        eatAndAdvance(isIdentifier(name));

        symbolTable.define(name, type, LOCAL.getSegment());
        while (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            advance();
            String nextName = getCurrentTokenValue();
            eatAndAdvance(isIdentifier(name));

            symbolTable.define(nextName, type, LOCAL.getSegment());
        }
        requireSymbol(SEMICOLON.getCharacter());
    }

    private void compileStatements() {
        switch (getEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            case IF:
                ifCounter++;
                compileIf();
                break;
            case WHILE:
                whileCounter++;
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
        String trueLabel = TRUE.toString() + ifCounter;
        String falseLabel = FALSE.toString() + ifCounter;
        String elseLabel = ELSE.toString() + ifCounter;

        requireSymbol(KeywordType.IF.getValue());
        requireSymbol(OPEN_PARENTHESIS.getCharacter());
        compileExpression();
        requireSymbol(CLOSE_PARENTHESIS.getCharacter());
        requireSymbol(OPEN_BRACE.getCharacter());

        vmWriter.writeIf(trueLabel);
        vmWriter.writeGoto(falseLabel);
        vmWriter.writeLabel(trueLabel); //if true continue else skip
        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }

        requireSymbol(CLOSE_BRACE.getCharacter());

        if (isCurrentTokenEqualTo(ELSE.getValue())) {
            vmWriter.writeGoto(elseLabel); //go to else end if you have done the if statement.
        }
        vmWriter.writeLabel(falseLabel);
        if (isCurrentTokenEqualTo(ELSE.getValue())) {
            compileElse();
            vmWriter.writeLabel(elseLabel);
        }
    }

    private void compileElse() {
        requireSymbol(ELSE.getValue());
        requireSymbol(OPEN_BRACE.getCharacter());

        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }

        requireSymbol(CLOSE_BRACE.getCharacter());
    }

    private void compileWhile() {
        String whileLabel = WHILE.toString() + "_" + whileCounter;
        String whileEndLabel = WHILE.toString() + "_END_" + whileCounter;

        requireSymbol(KeywordType.WHILE.getValue());
        requireSymbol(OPEN_PARENTHESIS.getCharacter());
        vmWriter.writeLabel(whileLabel);
        compileExpression();
        vmWriter.writeArithmatic(ArithmaticType.NOT); // not true go to end whileloop
        vmWriter.writeIf(whileEndLabel);
        requireSymbol(CLOSE_PARENTHESIS.getCharacter());
        requireSymbol(OPEN_BRACE.getCharacter());

        while (isValidEnumIgnoreCase(StatementType.class, getCurrentTokenValue())) {
            compileStatements();
        }

        requireSymbol(CLOSE_BRACE.getCharacter());
        vmWriter.writeGoto(whileLabel);
        vmWriter.writeLabel(whileEndLabel);
    }

    private void compileDo() {
        requireSymbol(KeywordType.DO.getValue());
        compileSubroutineCall(getCurrentTokenValue());
        requireSymbol(SEMICOLON.getCharacter());

        //pop dummy value from stack. by contract void methods will return zero
        vmWriter.writePop(TEMP.getSegment(),0);
    }

    private void compileSubroutineCall(String className) {
        String segment = symbolTable.kindOf(className);
        String object = symbolTable.typeOf(className);
        int index = symbolTable.indexOf(className);
        String functionName = "";

        if (!object.isEmpty() && !isCurrentTokenEqualTo(DOT.getCharacter())) {
            vmWriter.writePush(segment, index);
            className = object;
            functionName = getCurrentTokenValue();
            eatAndAdvance(isIdentifier(functionName));
        } else if (isIdentifier(getCurrentTokenValue()) && tokenizer.getCurrentToken().getTokenType() != TokenType.STRING_CONST) {
            className = getCurrentTokenValue();
            advance();
            if (isCurrentTokenEqualTo(DOT.getCharacter())) {
                advance();
                functionName = getCurrentTokenValue();
                eatAndAdvance(isIdentifier(functionName));
            } else {
                functionName = className;
                className = "";
                vmWriter.writePush(POINTER.getSegment(), 0); //push object on stack. this.
            }
        }

        if (isCurrentTokenEqualTo(DOT.getCharacter())) {
            advance();
            functionName = getCurrentTokenValue();
            className = object.isEmpty() ? className : object;
            eatAndAdvance(isIdentifier(functionName));
        }

        if (isCurrentTokenEqualTo(OPEN_PARENTHESIS.getCharacter())) {
            advance();
            compileExpressionList();
            requireSymbol((CLOSE_PARENTHESIS.getCharacter()));
        }

        if (!object.isEmpty()) {
            nArgs++; //if method call on object then object should be passed in the call.
            vmWriter.writeCall(className + DOT.getCharacter() + functionName, nArgs);
        } else if (tokenizer.getCurrentToken().getTokenType() == TokenType.STRING_CONST) {
            vmWriter.writeCall("String.new", 1);
        } else if (className.isEmpty()) {
            nArgs++;
            vmWriter.writeCall(getFunctionCall(functionName), nArgs);
        } else{
            vmWriter.writeCall(className + DOT.getCharacter() + functionName, nArgs);
        }
    }

    private void compileLet() {
        requireSymbol(KeywordType.LET.getValue());
        String varName = getCurrentTokenValue();
        eatAndAdvance(isIdentifier(varName));
        boolean isArray = isCurrentTokenEqualTo(OPEN_BRACKET.getCharacter());
        if (isArray) {
            vmWriter.writePush(symbolTable.kindOf(varName), symbolTable.indexOf(varName));

            requireSymbol(OPEN_BRACKET.getCharacter());
            compileExpression();
            requireSymbol(CLOSE_BRACKET.getCharacter());

            vmWriter.writeArithmatic(ArithmaticType.ADD);
        }
        requireSymbol(EQUALS.getCharacter());
        compileExpression();
        requireSymbol(SEMICOLON.getCharacter());
        if (isArray) {
            vmWriter.writePop(TEMP.getSegment(), 0);
            vmWriter.writePop(POINTER.getSegment(), 1);
            vmWriter.writePush(TEMP.getSegment(), 0);
            vmWriter.writePop(THAT.getSegment(), 0);
        } else {
            vmWriter.writePop(symbolTable.kindOf(varName), symbolTable.indexOf(varName));
        }
    }

    private void compileReturn() {
        requireSymbol(RETURN.getValue());
        if (!getCurrentTokenValue().equals(SEMICOLON.getCharacter())) {
            compileExpression();
        } else {
            //have to return something. Push zero by contract
            vmWriter.writePush(CONST.getSegment(), 0);
        }
        vmWriter.writeReturn();
        requireSymbol(SEMICOLON.getCharacter());
    }

    private void compileExpressionList() {
        nArgs = 0;
        //if closing parenthis no args
        if (!isCurrentTokenEqualTo(CLOSE_PARENTHESIS.getCharacter())) {
            nArgs++;
            compileExpression();
        }
    }

    private void compileExpression() {
        compileTerm();
        if (isCurrentTokenEqualTo(COMMA.getCharacter())) {
            requireSymbol(COMMA.getCharacter());
            nArgs++;
            compileExpression();
        }
    }

    private void compileTerm() {
        if (tokenizer.getCurrentToken().getTokenType() == TokenType.INT_CONST) {
            vmWriter.writePush(CONST.getSegment(), Integer.parseInt(getCurrentTokenValue()));
            advance();
        } else if (tokenizer.getCurrentToken().getTokenType() == TokenType.STRING_CONST) {
            char[] chars = getCurrentTokenValue().toCharArray();
            vmWriter.writePush(CONST.getSegment(), chars.length);
            compileSubroutineCall("");
            for (char character: chars) {
                vmWriter.writePush(CONST.getSegment(), (int) character); // push ascii code
                vmWriter.writeCall("String.appendChar", 2);
            }
            advance();
        } else if (StringUtils.equals(getCurrentTokenValue(), TRUE.getValue())) {
            vmWriter.writePush(CONST.getSegment(), 0); //evrything that is not zero is true.
            vmWriter.writeArithmatic(ArithmaticType.NOT);
            advance();
        } else if (StringUtils.equals(getCurrentTokenValue(), FALSE.getValue())) {
            vmWriter.writePush(CONST.getSegment(), 0);
            advance();
        }  else if (StringUtils.equals(getCurrentTokenValue(), NULL.getValue())) {
            vmWriter.writePush(CONST.getSegment(), 0);
            advance();
        }  else if (StringUtils.equals(getCurrentTokenValue(), THIS.getValue())) {
            vmWriter.writePush(POINTER.getSegment(), 0);
            advance();
        } else if (isIdentifier(getCurrentTokenValue())) {
            lookAhead();
        } else if (isCurrentTokenEqualTo(OPEN_PARENTHESIS.getCharacter())) {
            advance();
            compileExpression();
            requireSymbol(CLOSE_PARENTHESIS.getCharacter());
        } else if (isCurrentTokenEqualTo(SUB.getCharacter())) {
            advance();
            compileTerm();
            vmWriter.writeArithmatic(ArithmaticType.NEG);
        } else if (isCurrentTokenEqualTo(TILDE.getCharacter())) {
            advance();
            compileTerm();
            vmWriter.writeArithmatic(ArithmaticType.NOT);
        }
        if (isOp()) {
            String op = getCurrentTokenValue();
            advance();
            if (op.equals(MULTIPLY.getCharacter()) || op.equals(DIVIDE.getCharacter())) {
                //always 2 args- jack does not support multiply. uses os function
                compileTerm();
                vmWriter.writeCall(ArithmaticType.mappings.get(op).getVmValue(), 2);
            } else {
                compileTerm();
                vmWriter.writeArithmatic(ArithmaticType.mappings.get(op));
            }
        }
    }

    private void lookAhead() {
        String identifier = getCurrentTokenValue();
        String segment = symbolTable.kindOf(identifier);
        int index = symbolTable.indexOf(identifier);

        advance();
        if (isCurrentTokenEqualTo(OPEN_BRACKET.getCharacter())) {
            vmWriter.writePush(segment, index);

            requireSymbol(OPEN_BRACKET.getCharacter());
            compileExpression();
            requireSymbol(CLOSE_BRACKET.getCharacter());

            vmWriter.writeArithmatic(ArithmaticType.ADD);
            vmWriter.writePop(POINTER.getSegment(), 1); // pop/anchor base-address of array and index into that
            vmWriter.writePush(THAT.getSegment(), 0);

        } else if (isCurrentTokenEqualTo(DOT.getCharacter())) {
            if (!segment.isEmpty()) {
                vmWriter.writePush(segment, index);
            }
            compileSubroutineCall(identifier);
        } else {
            vmWriter.writePush(segment, index);
        }
    }

    private boolean isOp() {
        return SymbolType.getOperators().stream().anyMatch(symbolType -> isCurrentTokenEqualTo(symbolType.getCharacter()));
    }

    private void requireSymbol(String value) {
        if (isCurrentTokenEqualTo(value)) {
            advance();
        } else {
            error(value);
        }
    }

    private void eatAndAdvance(boolean eat) {
        Token currentToken = tokenizer.getCurrentToken();
        if (eat) {
            advance();
        } else {
            error(currentToken.getValue());
        }
    }

    private void advance() {
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        }
    }

    private void error(String value) {
        throw new IllegalStateException("Expected value: " + value + " was: " + tokenizer.getCurrentToken().getValue());
    }

    private boolean isCurrentTokenEqualTo(String value) {
        return StringUtils.equals(getCurrentTokenValue(), value);
    }

    private String getCurrentTokenValue() {
        return tokenizer.getCurrentToken().getValue();
    }
}