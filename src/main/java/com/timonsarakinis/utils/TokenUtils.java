package com.timonsarakinis.utils;

import com.timonsarakinis.tokens.types.KeywordType;
import com.timonsarakinis.tokens.types.SymbolType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.timonsarakinis.tokens.types.SymbolType.*;
import static com.timonsarakinis.tokens.types.TokenType.*;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.EnumUtils.isValidEnumIgnoreCase;
import static org.apache.commons.lang3.StringUtils.*;

public class TokenUtils {
    public static final String PLACE_HOLDER = "^";

    public static String removeNoneTokens(List<String> lines) {
        String unrefinedTokens = lines.stream()
                .filter(line -> isNotBlank(line) || !line.startsWith("/"))
                .map(line -> replaceIgnoreCase(line, "\t", " "))
                .map(line -> substringBefore(line, "//"))
                .map(line -> {
                    if (line.contains("\"")) {
                        line = line.trim();
                        String stringConstant = line.substring(line.indexOf("\""), line.lastIndexOf("\"") + 1);
                        String placeHolder = " " + stringConstant.replace(" ", PLACE_HOLDER) + " ";
                        line = StringUtils.replace(line, stringConstant, placeHolder);
                    }
                    return line;
                })
                .map(String::trim)
                .collect(joining(" "));

        String removeApiComments = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
        return unrefinedTokens.replaceAll(removeApiComments, "");
    }

    public static List<String> splitIntoTokens(String lines) {
        lines = addSpacesToSymbolsForSplit(lines);

        return Stream.of(lines.split(" "))
                .filter(StringUtils::isNotBlank)
                .map(stringConstants -> stringConstants.replace(PLACE_HOLDER, " "))
                .collect(Collectors.toList());
    }

    private static String addSpacesToSymbolsForSplit(String lines) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (String s : lines.split(" ")) {
            if (!s.contains("\"")) {
                s = s.replace(";", (" " + SEMICOLON.getCharacter()) + " ");
                s = s.replace("&", (" " + AMPERSAND.getCharacter()) + " ");
                s = s.replace(",", (" " + COMMA.getCharacter()) + " ");
                s = s.replace("(", (" " + OPEN_PARENTHESIS.getCharacter()) + " ");
                s = s.replace(")", (" " + CLOSE_PARENTHESIS.getCharacter()) + " ");
                s = s.replace(".", (" " + DOT.getCharacter()) + " ");
                s = s.replace("<", (" " + LESS_THAN.getCharacter()) + " ");
                s = s.replace(">", (" " + GREATER_THAN.getCharacter()) + " ");
                s = s.replace("-", (" " + SUB.getCharacter()) + " ");
                s = s.replace("[", (" " + OPEN_BRACKET.getCharacter()) + " ");
                s = s.replace("]", (" " + CLOSE_BRACKET.getCharacter()) + " ");
                s = s.replace("~", (" " + TILDE.getCharacter()) + " ");
                s = s.trim();
            }
            stringJoiner.add(s);
        }
        return stringJoiner.toString();
    }

    public static String FindTokenType(String token) {
        if (SymbolType.getCharacters().contains(token)) {
            return SYMBOL.toString();
        } else if (isValidEnumIgnoreCase(KeywordType.class, token)) {
            return KEYWORD.toString();
        } else if (isNumeric(token)) {
            return INT_CONST.toString();
        } else if (token.indexOf("\"") == 0) {
            return STRING_CONST.toString();
        } else {
            return IDENTIFIER.toString();
        }
    }
}
