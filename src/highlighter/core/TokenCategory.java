package highlighter.core;

// Token kategorilerini tanımlayan enum
public enum TokenCategory {
    KEYWORD,      // Anahtar kelimeler
    IDENTIFIER,   // Tanımlayıcılar
    LITERAL,      // Literaller
    OPERATOR,     // Operatörler
    DELIMITER,    // Ayraclar
    COMMENT,      // Yorumlar
    EOF,          // Dosya sonu
    UNKNOWN       // Tanımlanamayan token'lar
}