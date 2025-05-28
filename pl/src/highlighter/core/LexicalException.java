package highlighter.core;

// Sözdizimi çözümleme hatalarını temsil eder
public class LexicalException extends Exception {
    private final int lineNumber;
    private final int position;

    // Temel hata mesajı için kurucu
    public LexicalException(String message) {
        super(message);
        this.lineNumber = -1;
        this.position = -1;
    }

    // Satır ve pozisyon bilgisi ile hata mesajı için kurucu
    public LexicalException(String message, int lineNumber, int position) {
        super(String.format("Satır %d, pozisyon %d: %s", lineNumber, position, message));
        this.lineNumber = lineNumber;
        this.position = position;
    }

    // Satır numarasını döndürür
    public int getLineNumber() {
        return lineNumber;
    }

    // Pozisyonu döndürür
    public int getPosition() {
        return position;
    }
}