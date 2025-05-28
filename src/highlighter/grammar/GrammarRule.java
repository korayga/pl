package highlighter.grammar;

import java.util.List;

// Dilbilgisi kuralını temsil eder
public class GrammarRule {
    private final String lhs; // Sol taraf (örneğin: expr, stmt)
    private final List<String> rhs; // Sağ taraf (örneğin: ["expr", "+", "term"])

    // Kurucu metod
    public GrammarRule(String lhs, List<String> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    // Sol tarafı döndürür
    public String getLhs() {
        return lhs;
    }

    // Sağ tarafı döndürür
    public List<String> getRhs() {
        return rhs;
    }

    // Kuralı string olarak döndürür
    @Override
    public String toString() {
        return lhs + " -> " + String.join(" ", rhs);
    }
}