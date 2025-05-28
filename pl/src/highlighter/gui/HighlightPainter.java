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
        // Prism.js tarzı modern renk tonları
        colorMap.put(TokenType.KEYWORD, new Color(189, 147, 249)); // Mor
        colorMap.put(TokenType.IDENTIFIER, Color.WHITE);           // Beyaz
        colorMap.put(TokenType.INTEGER_LITERAL, new Color(139, 233, 253)); // Cyan
        colorMap.put(TokenType.FLOAT_LITERAL, new Color(139, 233, 253));
        colorMap.put(TokenType.STRING_LITERAL, Color.YELLOW); // Sarı
        colorMap.put(TokenType.CHAR_LITERAL, new Color(241, 250, 140));
        colorMap.put(TokenType.LINE_COMMENT, new Color(98, 114, 164)); // Gri-mavi
        colorMap.put(TokenType.BLOCK_COMMENT, new Color(98, 114, 164));
        colorMap.put(TokenType.UNKNOWN,Color.RED); // Kırmızı
        colorMap.put(TokenType.EOF, Color.GRAY);
        colorMap.put(TokenType.INCREMENT, new Color(255, 121, 198));
        colorMap.put(TokenType.DECREMENT, new Color(255, 121, 198));

        // Operatörler için pembe renk
        for (TokenType type : TokenType.values()) {
            if (type.name().contains("PLUS") || type.name().contains("MINUS") ||
                    type.name().contains("MULTIPLY") || type.name().contains("DIVIDE") ||
                    type.name().contains("ASSIGN") || type.name().contains("EQUAL") ||
                    type.name().contains("MODULO") || type.name().contains("GREATER") ||
                    type.name().contains("LESS") || type.name().contains("SHIFT") ||
                    type.name().contains("LOGICAL") || type.name().contains("BITWISE")) {
                colorMap.put(type, new Color(255, 121, 198));
            }
        }

        // Parantezler ve ayraçlar için açık gri
        for (TokenType type : new TokenType[]{
                TokenType.LPAREN, TokenType.RPAREN, TokenType.LBRACE, TokenType.RBRACE,
                TokenType.LBRACKET, TokenType.RBRACKET, TokenType.SEMICOLON,
                TokenType.COMMA, TokenType.DOT
        }) {
            colorMap.put(type, new Color(171, 178, 191));
        }
    }

    // Token türüne göre rengi döndürür
    public static Color getColor(TokenType type) {
        return colorMap.getOrDefault(type, Color.LIGHT_GRAY);
    }
}