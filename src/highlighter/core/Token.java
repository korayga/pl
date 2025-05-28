package highlighter.core;

// Sözdizimi çözümleme için token nesnesi
public class Token {
    private final TokenType type; // Token türü
    private final String value;   // Token değeri
    private final int startIndex; // Başlangıç pozisyonu
    private final int endIndex;   // Bitiş pozisyonu

    // Kurucu metod
    public Token(TokenType type, String value, int startIndex, int endIndex) {
        this.type = type;
        this.value = value;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    // Token türünü döndürür
    public TokenType getType() {
        return type;
    }

    // Token değerini döndürür
    public String getValue() {
        return value;
    }

    // Başlangıç pozisyonunu döndürür
    public int getStartIndex() {
        return startIndex;
    }

    // Bitiş pozisyonunu döndürür
    public int getEndIndex() {
        return endIndex;
    }

    // Token bilgisini string olarak döndürür
    @Override
    public String toString() {
        return String.format("Token(%s, '%s', %d-%d)", type, value, startIndex, endIndex);
    }
}