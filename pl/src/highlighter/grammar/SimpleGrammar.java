package highlighter.grammar;

import java.util.ArrayList;
import java.util.List;

public class SimpleGrammar {

    public static List<GrammarRule> getRules() {
        List<GrammarRule> rules = new ArrayList<>();

        // Expressions with comparisons
        rules.add(new GrammarRule("expr", List.of("comp")));
        rules.add(new GrammarRule("comp", List.of("comp", "<", "additive")));
        rules.add(new GrammarRule("comp", List.of("comp", ">", "additive")));
        rules.add(new GrammarRule("comp", List.of("comp", "<=", "additive")));
        rules.add(new GrammarRule("comp", List.of("comp", ">=", "additive")));
        rules.add(new GrammarRule("comp", List.of("comp", "==", "additive")));
        rules.add(new GrammarRule("comp", List.of("comp", "!=", "additive")));
        rules.add(new GrammarRule("comp", List.of("additive")));

        // Additive expressions
        rules.add(new GrammarRule("additive", List.of("additive", "+", "term")));
        rules.add(new GrammarRule("additive", List.of("additive", "-", "term")));
        rules.add(new GrammarRule("additive", List.of("term")));

        // Terms
        rules.add(new GrammarRule("term", List.of("term", "*", "factor")));
        rules.add(new GrammarRule("term", List.of("term", "/", "factor")));
        rules.add(new GrammarRule("term", List.of("factor")));

        // Factors
        rules.add(new GrammarRule("factor", List.of("(", "expr", ")")));
        rules.add(new GrammarRule("factor", List.of("IDENTIFIER")));
        rules.add(new GrammarRule("factor", List.of("INTEGER_LITERAL")));

        // Statements
        rules.add(new GrammarRule("stmt", List.of("if", "(", "expr", ")", "block")));
        rules.add(new GrammarRule("stmt", List.of("while", "(", "expr", ")", "block")));
        rules.add(new GrammarRule("stmt", List.of("for", "(", "stmt", "expr", ";", "expr", ")", "block")));
        rules.add(new GrammarRule("stmt", List.of("return", "expr", ";")));
        rules.add(new GrammarRule("stmt", List.of("expr", ";")));
        rules.add(new GrammarRule("stmt", List.of("block")));

        // Declarations
        rules.add(new GrammarRule("decl", List.of("type", "IDENTIFIER", ";")));
        rules.add(new GrammarRule("decl", List.of("type", "IDENTIFIER", "=", "expr", ";")));

        // Types
        rules.add(new GrammarRule("type", List.of("int")));
        rules.add(new GrammarRule("type", List.of("float")));
        rules.add(new GrammarRule("type", List.of("boolean")));
        rules.add(new GrammarRule("type", List.of("char")));

        // Blocks
        rules.add(new GrammarRule("block", List.of("{", "stmt_list", "}")));
        rules.add(new GrammarRule("stmt_list", List.of("stmt_list", "stmt")));
        rules.add(new GrammarRule("stmt_list", List.of("stmt")));

        return rules;
    }
}