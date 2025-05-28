package highlighter.core;

// Token kategorilerini tanımlayan enum
public enum TokenCategory {
    KEYWORD,      // Anahtar kelimeler
    IDENTIFIER,   // Tanımlayıcılar
    LITERAL,      // Literaller (sayı, string, vb.)
    OPERATOR,     // Operatörler
    DELIMITER,    // Ayraclar (parantezler, noktalama)
    COMMENT,      // Yorumlar
    EOF,          // Dosya sonu
    UNKNOWN       // Tanımlanamayan token'lar
}