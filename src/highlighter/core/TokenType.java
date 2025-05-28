package highlighter.core;

// Token türlerini tanımlayan enum
public enum TokenType {
    // Literaller
    IDENTIFIER,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    CHAR_LITERAL,

    // Anahtar kelimeler
    KEYWORD,

    // Operatörler
    ASSIGN,                // =
    PLUS,                  // +
    MINUS,                 // -
    MULTIPLY,              // *
    DIVIDE,                // /
    MODULO,                // %

    EQUAL_TO,              // ==
    NOT_EQUAL_TO,          // !=
    GREATER_THAN,          // >
    LESS_THAN,             // <
    GREATER_THAN_OR_EQUAL, // >=
    LESS_THAN_OR_EQUAL,    // <=

    PLUS_ASSIGN,           // +=
    MINUS_ASSIGN,          // -=
    MULTIPLY_ASSIGN,       // *=
    DIVIDE_ASSIGN,         // /=
    MODULO_ASSIGN,         // %=

    INCREMENT,             // ++
    DECREMENT,             // --

    LOGICAL_AND,           // &&
    LOGICAL_OR,            // ||
    LOGICAL_NOT,           // !

    BITWISE_AND,           // &
    BITWISE_OR,            // |
    BITWISE_XOR,           // ^
    BITWISE_NOT,           // ~

    LEFT_SHIFT,            // <<
    RIGHT_SHIFT,           // >>
    UNSIGNED_RIGHT_SHIFT,  // >>>

    // Noktalama işaretleri
    LPAREN,                // (
    RPAREN,                // )
    LBRACE,                // {
    RBRACE,                // }
    LBRACKET,              // [
    RBRACKET,              // ]
    SEMICOLON,             // ;
    COMMA,                 // ,
    DOT,                   // .

    // Yorumlar
    LINE_COMMENT,
    BLOCK_COMMENT,

    // Özel token'lar
    UNKNOWN,
    EOF;
}