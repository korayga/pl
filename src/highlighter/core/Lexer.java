package highlighter.core;

import java.util.*;

public class Lexer { // Sözdizimi çözümleme sınıfı sebesta programming language kitabı 4. ünite referans alınarak tasarlanmıştır.

    private static final int LETTER_LIKE = 0;
    private static final int DIGIT = 1;
    private static final int OTHER = 99;
    private static final int EOF_CHAR_CLASS = -1;

    // Java anahtar kelimeleri
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "continue", "for", "new", "switch",
            "assert", "default", "package", "synchronized",
            "boolean", "do", "if", "private", "this",
            "break", "double", "implements", "protected", "throw",
            "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void",
            "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while"
    ));

    private String input;
    private int pos;
    private char currentChar;
    private int charClass;
    private int lineNumber;

    public Lexer() {
        this.lineNumber = 1;
    }

        // Sonraki karakteri okumak için
    private void advance() {
        if (pos < input.length()) {
            currentChar = input.charAt(pos);
            if (currentChar == '\n') lineNumber++; //eğer alt satıra geçiyorsa line numarası artırılır
            pos++;
            updateCharClass();
        } else {
            currentChar = '\0';
            charClass = EOF_CHAR_CLASS;
        }
    }
    // Sonraki karakterin sınıfını bulma
    private void updateCharClass() {
        if (Character.isLetter(currentChar) || currentChar == '_' || currentChar == '$') {
            charClass = LETTER_LIKE;
        } else if (Character.isDigit(currentChar)) {
            charClass = DIGIT;
        } else {
            charClass = OTHER;
        }
    }
    // Sonraki karakterleri almak  için peek fonksiyonları
    private char peek() {
        return (pos < input.length()) ? input.charAt(pos) : '\0';
    }

    private char peekNext() {
        return (pos + 1 < input.length()) ? input.charAt(pos + 1) : '\0';
    }
    // Token oluşturma
    private Token createToken(TokenType type, String value, int start) {
        int end = start + value.length() - 1;
        return new Token(type, value, start, end);
    }

    public List<Token> tokenize(String text) throws LexicalException {
        this.input = text;
        this.pos = 0;
        this.lineNumber = 1;
        List<Token> tokens = new ArrayList<>();

        if (input.length() > 0) {
            advance();
        }

        while (charClass != EOF_CHAR_CLASS) {
            int start = pos - 1; // Şu anki karakterin başlangıç pozisyonu
            StringBuilder buffer = new StringBuilder(); //string işlemlerini yapabilmek ve kolaylaştırmak için string builder kullandım

            // Boşluk karakterlerini atla
            if (Character.isWhitespace(currentChar)) {
                advance();
                continue;
            }

            // Yorum satırları
            if (currentChar == '/') {
                if (peek() == '/') {
                    // Tek satır yorum
                    buffer.append(currentChar); advance();
                    buffer.append(currentChar); advance();
                    while (currentChar != '\n' && charClass != EOF_CHAR_CLASS) {
                        buffer.append(currentChar);
                        advance();
                    }
                    tokens.add(createToken(TokenType.LINE_COMMENT, buffer.toString(), start));
                    continue;
                } else if (peek() == '*') {
                    // Çok satır yorum
                    buffer.append(currentChar); advance();
                    buffer.append(currentChar); advance();
                    while (charClass != EOF_CHAR_CLASS) {
                        if (currentChar == '*' && peek() == '/') {
                            buffer.append(currentChar); advance();
                            buffer.append(currentChar); advance();
                            break;
                        }
                        buffer.append(currentChar);
                        advance();
                    }
                    tokens.add(createToken(TokenType.BLOCK_COMMENT, buffer.toString(), start));
                    continue;
                }
            }

            // Tanımlayıcılar ve anahtar kelimeler
            if (charClass == LETTER_LIKE) {
                while (charClass == LETTER_LIKE || charClass == DIGIT) {
                    buffer.append(currentChar);
                    advance();
                }
                String value = buffer.toString();
                TokenType type = JAVA_KEYWORDS.contains(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(createToken(type, value, start));
                continue;
            }

            // Sayısal değerler
            if (charClass == DIGIT) {
                boolean isFloat = false;
                while (charClass == DIGIT) {
                    buffer.append(currentChar);
                    advance();
                }
                if (currentChar == '.' && Character.isDigit(peek())) {
                    isFloat = true;
                    buffer.append(currentChar);
                    advance();
                    while (charClass == DIGIT) {
                        buffer.append(currentChar);
                        advance();
                    }
                }
                if ("fFdDlL".indexOf(currentChar) != -1) { // float, double, long, float literal'larını tespit ediyorum
                    buffer.append(currentChar);
                    advance();
                }
                tokens.add(createToken(isFloat ? TokenType.FLOAT_LITERAL : TokenType.INTEGER_LITERAL, buffer.toString(), start));
                continue;
            }

            // String literalleri
            if (currentChar == '"') {
                buffer.append(currentChar);
                advance();
                while (currentChar != '"' && charClass != EOF_CHAR_CLASS) {
                    if (currentChar == '\\' && charClass != EOF_CHAR_CLASS) {
                        buffer.append(currentChar);
                        advance();
                        if (charClass != EOF_CHAR_CLASS) {
                            buffer.append(currentChar);
                            advance();
                        }
                    } else {
                        buffer.append(currentChar);
                        advance();
                    }
                }
                if (currentChar != '"') {
                    throw new LexicalException("Unclosed string literal", lineNumber, start);
                }
                buffer.append(currentChar);
                advance();
                tokens.add(createToken(TokenType.STRING_LITERAL, buffer.toString(), start));
                continue;
            }

            // Karakter literalleri
            if (currentChar == '\'') {
                buffer.append(currentChar);
                advance();
                if (currentChar == '\\' && charClass != EOF_CHAR_CLASS) {
                    buffer.append(currentChar);
                    advance();
                }
                if (charClass != EOF_CHAR_CLASS) {
                    buffer.append(currentChar);
                    advance();
                }
                if (currentChar != '\'') {
                    throw new LexicalException("Unclosed character literal", lineNumber, start);
                }
                buffer.append(currentChar);
                advance();
                tokens.add(createToken(TokenType.CHAR_LITERAL, buffer.toString(), start));
                continue;
            }

            // Operatörler
            String op3 = "" + currentChar + peek() + peekNext();
            String op2 = "" + currentChar + peek();
            String op1 = "" + currentChar;

            if (op3.equals(">>>")) {
                tokens.add(createToken(TokenType.UNSIGNED_RIGHT_SHIFT, op3, start));
                advance(); advance(); advance();
                continue;
            }

            switch (op2) {
                case "==": tokens.add(createToken(TokenType.EQUAL_TO, op2, start)); advance(); advance(); continue;
                case "!=": tokens.add(createToken(TokenType.NOT_EQUAL_TO, op2, start)); advance(); advance(); continue;
                case ">=": tokens.add(createToken(TokenType.GREATER_THAN_OR_EQUAL, op2, start)); advance(); advance(); continue;
                case "<=": tokens.add(createToken(TokenType.LESS_THAN_OR_EQUAL, op2, start)); advance(); advance(); continue;
                case "&&": tokens.add(createToken(TokenType.LOGICAL_AND, op2, start)); advance(); advance(); continue;
                case "||": tokens.add(createToken(TokenType.LOGICAL_OR, op2, start)); advance(); advance(); continue;
                case "++": tokens.add(createToken(TokenType.INCREMENT, op2, start)); advance(); advance(); continue;
                case "--": tokens.add(createToken(TokenType.DECREMENT, op2, start)); advance(); advance(); continue;
                case "+=": tokens.add(createToken(TokenType.PLUS_ASSIGN, op2, start)); advance(); advance(); continue;
                case "-=": tokens.add(createToken(TokenType.MINUS_ASSIGN, op2, start)); advance(); advance(); continue;
                case "*=": tokens.add(createToken(TokenType.MULTIPLY_ASSIGN, op2, start)); advance(); advance(); continue;
                case "/=": tokens.add(createToken(TokenType.DIVIDE_ASSIGN, op2, start)); advance(); advance(); continue;
                case "%=": tokens.add(createToken(TokenType.MODULO_ASSIGN, op2, start)); advance(); advance(); continue;
                case "<<": tokens.add(createToken(TokenType.LEFT_SHIFT, op2, start)); advance(); advance(); continue;
                case ">>": tokens.add(createToken(TokenType.RIGHT_SHIFT, op2, start)); advance(); advance(); continue;
            }

            switch (currentChar) {
                case '.': tokens.add(createToken(TokenType.DOT, op1, start)); break;
                case '+': tokens.add(createToken(TokenType.PLUS, op1, start)); break;
                case '-': tokens.add(createToken(TokenType.MINUS, op1, start)); break;
                case '*': tokens.add(createToken(TokenType.MULTIPLY, op1, start)); break;
                case '/': tokens.add(createToken(TokenType.DIVIDE, op1, start)); break;
                case '%': tokens.add(createToken(TokenType.MODULO, op1, start)); break;
                case '=': tokens.add(createToken(TokenType.ASSIGN, op1, start)); break;
                case '<': tokens.add(createToken(TokenType.LESS_THAN, op1, start)); break;
                case '>': tokens.add(createToken(TokenType.GREATER_THAN, op1, start)); break;
                case '!': tokens.add(createToken(TokenType.LOGICAL_NOT, op1, start)); break;
                case '&': tokens.add(createToken(TokenType.BITWISE_AND, op1, start)); break;
                case '|': tokens.add(createToken(TokenType.BITWISE_OR, op1, start)); break;
                case '^': tokens.add(createToken(TokenType.BITWISE_XOR, op1, start)); break;
                case '~': tokens.add(createToken(TokenType.BITWISE_NOT, op1, start)); break;
                case '(': tokens.add(createToken(TokenType.LPAREN, op1, start)); break;
                case ')': tokens.add(createToken(TokenType.RPAREN, op1, start)); break;
                case '{': tokens.add(createToken(TokenType.LBRACE, op1, start)); break;
                case '}': tokens.add(createToken(TokenType.RBRACE, op1, start)); break;
                case '[': tokens.add(createToken(TokenType.LBRACKET, op1, start)); break;
                case ']': tokens.add(createToken(TokenType.RBRACKET, op1, start)); break;
                case ';': tokens.add(createToken(TokenType.SEMICOLON, op1, start)); break;
                case ',': tokens.add(createToken(TokenType.COMMA, op1, start)); break;
                default:
                    tokens.add(createToken(TokenType.UNKNOWN, op1, start));
                    throw new LexicalException("Unrecognized character '" + currentChar + "'", lineNumber, start);
            }
            advance();
        }

        tokens.add(new Token(TokenType.EOF, "EOF", pos, pos));
        return tokens;
    }

    public static TokenCategory getCategory(TokenType type) {
        if (type == TokenType.KEYWORD) return TokenCategory.KEYWORD;
        if (type == TokenType.IDENTIFIER) return TokenCategory.IDENTIFIER;
        if (type == TokenType.INTEGER_LITERAL || type == TokenType.FLOAT_LITERAL ||
                type == TokenType.STRING_LITERAL || type == TokenType.CHAR_LITERAL) return TokenCategory.LITERAL;
        if (type == TokenType.LINE_COMMENT || type == TokenType.BLOCK_COMMENT) return TokenCategory.COMMENT;
        if (type == TokenType.EOF) return TokenCategory.EOF;
        if (type == TokenType.UNKNOWN) return TokenCategory.UNKNOWN;
        return TokenCategory.OPERATOR;
    }
}