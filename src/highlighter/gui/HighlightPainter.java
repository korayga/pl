package highlighter.gui;

import highlighter.core.TokenType;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

// Token'lar için renk eşlemelerini sağlayan sınıf
public class HighlightPainter {
    private static final Map<TokenType, Color> colorMap = new EnumMap<>(TokenType.class);

    // Varsayılan renk şemasını yükler
    static {
        colorMap.put(TokenType.KEYWORD, Color.MAGENTA);
        colorMap.put(TokenType.IDENTIFIER, Color.WHITE);
        colorMap.put(TokenType.INTEGER_LITERAL, Color.CYAN);
        colorMap.put(TokenType.FLOAT_LITERAL, Color.CYAN);
        colorMap.put(TokenType.STRING_LITERAL, Color.YELLOW);
        colorMap.put(TokenType.CHAR_LITERAL, Color.YELLOW);
        colorMap.put(TokenType.LINE_COMMENT,Color.WHITE);
        colorMap.put(TokenType.BLOCK_COMMENT, Color.WHITE);
        colorMap.put(TokenType.UNKNOWN,Color.RED);
        colorMap.put(TokenType.EOF, Color.GRAY);
        colorMap.put(TokenType.INCREMENT, Color.PINK);
        colorMap.put(TokenType.DECREMENT, Color.PINK);

        // Operatörler için pembe renk
        for (TokenType type : TokenType.values()) {
            if (type.name().contains("PLUS") || type.name().contains("MINUS") ||
                    type.name().contains("MULTIPLY") || type.name().contains("DIVIDE") ||
                    type.name().contains("ASSIGN") || type.name().contains("EQUAL") ||
                    type.name().contains("MODULO") || type.name().contains("GREATER") ||
                    type.name().contains("LESS") || type.name().contains("SHIFT") ||
                    type.name().contains("LOGICAL") || type.name().contains("BITWISE")) {
                colorMap.put(type, Color.PINK);
            }
        }

        // Parantezler ve ayraçlar için açık gri
        for (TokenType type : new TokenType[]{
                TokenType.LPAREN, TokenType.RPAREN, TokenType.LBRACE, TokenType.RBRACE,
                TokenType.LBRACKET, TokenType.RBRACKET, TokenType.SEMICOLON,
                TokenType.COMMA, TokenType.DOT
        }) {
            colorMap.put(type, Color.WHITE);
        }
    }

    // Token türüne göre rengi döndürür
    public static Color getColor(TokenType type) {
        return colorMap.getOrDefault(type, Color.LIGHT_GRAY);
    }
}