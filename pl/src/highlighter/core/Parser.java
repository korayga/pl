
package highlighter.core;

import highlighter.grammar.SimpleGrammar;
import highlighter.grammar.GrammarRule;

import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int index;
    private Token current;
    private final List<String> errors;
    private final List<GrammarRule> grammar;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.current = tokens.isEmpty() ? new Token(TokenType.EOF, "EOF", -1, -1) : tokens.get(0);
        this.errors = new ArrayList<>();
        this.grammar = SimpleGrammar.getRules();
    }

    private void advance() {
        if (index < tokens.size() - 1) {
            index++;
            current = tokens.get(index);
        } else {
            current = new Token(TokenType.EOF, "EOF", -1, -1);
        }
    }

    public List<String> parse() {
        errors.clear();
        try {
            parseStmtList();
            if (current.getType() != TokenType.EOF) {
                errors.add("Unexpected token after end of input: " + current.getValue() + " at position " + current.getStartIndex());
            }
        } catch (ParseException e) {
            errors.add(e.getMessage());
        }
        return errors;
    }

    private void parseStmtList() throws ParseException {
        while (current.getType() != TokenType.EOF && current.getType() != TokenType.RBRACE) {
            parseStmt();
        }
    }

    private void parseStmt() throws ParseException {
        try {
            if (current.getType() == TokenType.KEYWORD) {
                String keywordValue = current.getValue();
                switch (keywordValue) {
                    case "if":
                        parseIfStmt();
                        break;
                    case "while":
                        parseWhileStmt();
                        break;
                    case "for":
                        parseForStmt();
                        break;
                    case "return":
                        parseReturnStmt();
                        break;
                    default:
                        if (isType(keywordValue)) {
                            parseDecl();
                        } else {
                            throw new ParseException("Invalid statement keyword: " + keywordValue + " at position " + current.getStartIndex());
                        }
                        break;
                }
            } else if (current.getType() == TokenType.LBRACE) {
                parseBlock();
            } else if (current.getType() == TokenType.IDENTIFIER) {
                parseExprStmt();
            } else if (current.getType() == TokenType.EOF || current.getType() == TokenType.RBRACE) {
                return; // Normal sonlanma
            } else {
                throw new ParseException("Unexpected token: " + current.getValue() + " at position " + current.getStartIndex());
            }
        } catch (ParseException e) {
            // Hata durumunda bir sonraki statement'a atla
            skipToNextStatement();
            throw e;
        }
    }

    private void skipToNextStatement() {
        while (current.getType() != TokenType.EOF &&
                current.getType() != TokenType.SEMICOLON &&
                current.getType() != TokenType.RBRACE &&
                current.getType() != TokenType.LBRACE) {
            advance();
        }
        if (current.getType() == TokenType.SEMICOLON) {
            advance();
        }
    }

    private void parseIfStmt() throws ParseException {
        expect(TokenType.KEYWORD, "if");
        expect(TokenType.LPAREN);
        parseExpr();
        expect(TokenType.RPAREN);
        parseStmt(); // Statement or block

        // Optional else clause
        if (current.getType() == TokenType.KEYWORD && current.getValue().equals("else")) {
            advance();
            parseStmt();
        }
    }

    private void parseWhileStmt() throws ParseException {
        expect(TokenType.KEYWORD, "while");
        expect(TokenType.LPAREN);
        parseExpr();
        expect(TokenType.RPAREN);
        parseStmt(); // Statement or block
    }

    private void parseForStmt() throws ParseException {
        expect(TokenType.KEYWORD, "for");
        expect(TokenType.LPAREN);

        // Initialization (optional)
        if (current.getType() != TokenType.SEMICOLON) {
            if (isType(current.getValue())) {
                parseDecl(); // This will consume the semicolon
            } else {
                parseExpr();
                expect(TokenType.SEMICOLON);
            }
        } else {
            expect(TokenType.SEMICOLON);
        }

        // Condition (optional)
        if (current.getType() != TokenType.SEMICOLON) {
            parseExpr();
        }
        expect(TokenType.SEMICOLON);

        // Update (optional)
        if (current.getType() != TokenType.RPAREN) {
            parseExpr();
        }
        expect(TokenType.RPAREN);

        parseStmt(); // Statement or block
    }

    private void parseReturnStmt() throws ParseException {
        expect(TokenType.KEYWORD, "return");

        // Return expression is optional
        if (current.getType() != TokenType.SEMICOLON) {
            parseExpr();
        }
        expect(TokenType.SEMICOLON);
    }

    private void parseDecl() throws ParseException {
        expect(TokenType.KEYWORD); // type
        expect(TokenType.IDENTIFIER);

        // Optional initialization
        if (current.getType() == TokenType.ASSIGN) {
            advance();
            parseExpr();
        }
        expect(TokenType.SEMICOLON);
    }

    private void parseExprStmt() throws ParseException {
        parseExpr();
        expect(TokenType.SEMICOLON);
    }

    private void parseBlock() throws ParseException {
        expect(TokenType.LBRACE);
        parseStmtList();
        expect(TokenType.RBRACE);
    }

    private void parseExpr() throws ParseException {
        parseLogicalOr();
    }

    private void parseLogicalOr() throws ParseException {
        parseLogicalAnd();
        while (current.getType() == TokenType.LOGICAL_OR) {
            advance();
            parseLogicalAnd();
        }
    }

    private void parseLogicalAnd() throws ParseException {
        parseComparison();
        while (current.getType() == TokenType.LOGICAL_AND) {
            advance();
            parseComparison();
        }
    }

    private void parseComparison() throws ParseException {
        parseAdditive();
        while (current.getType() == TokenType.LESS_THAN ||
                current.getType() == TokenType.GREATER_THAN ||
                current.getType() == TokenType.LESS_THAN_OR_EQUAL ||
                current.getType() == TokenType.GREATER_THAN_OR_EQUAL ||
                current.getType() == TokenType.EQUAL_TO ||
                current.getType() == TokenType.NOT_EQUAL_TO) {
            advance();
            parseAdditive();
        }
    }

    private void parseAdditive() throws ParseException {
        parseTerm();
        while (current.getType() == TokenType.PLUS || current.getType() == TokenType.MINUS) {
            advance();
            parseTerm();
        }
    }

    private void parseTerm() throws ParseException {
        parseFactor();
        while (current.getType() == TokenType.MULTIPLY ||
                current.getType() == TokenType.DIVIDE ||
                current.getType() == TokenType.MODULO) {
            advance();
            parseFactor();
        }
    }

    private void parseFactor() throws ParseException {
        // Unary operators
        if (current.getType() == TokenType.LOGICAL_NOT ||
                current.getType() == TokenType.MINUS ||
                current.getType() == TokenType.PLUS) {
            advance();
            parseFactor();
            return;
        }

        if (current.getType() == TokenType.LPAREN) {
            advance();
            parseExpr();
            expect(TokenType.RPAREN);
        } else if (current.getType() == TokenType.IDENTIFIER) {
            advance();
            // Post-increment/decrement
            if (current.getType() == TokenType.INCREMENT || current.getType() == TokenType.DECREMENT) {
                advance();
            }
        } else if (current.getType() == TokenType.INTEGER_LITERAL ||
                current.getType() == TokenType.FLOAT_LITERAL ||
                current.getType() == TokenType.STRING_LITERAL ||
                current.getType() == TokenType.CHAR_LITERAL) {
            advance();
        } else if (current.getType() == TokenType.INCREMENT || current.getType() == TokenType.DECREMENT) {
            // Pre-increment/decrement
            advance();
            if (current.getType() == TokenType.IDENTIFIER) {
                advance();
            } else {
                throw new ParseException("Expected identifier after " + tokens.get(index-1).getValue() + " at position " + current.getStartIndex());
            }
        } else {
            throw new ParseException("Expected identifier, number, string, or parenthesized expression, got: " + current.getValue() + " at position " + current.getStartIndex());
        }
    }

    private void expect(TokenType type) throws ParseException {
        if (current.getType() != type) {
            throw new ParseException("Expected " + type + ", got: " + current.getValue() + " (" + current.getType() + ") at position " + current.getStartIndex());
        }
        advance();
    }

    private void expect(TokenType type, String value) throws ParseException {
        if (current.getType() != type || !current.getValue().equals(value)) {
            throw new ParseException("Expected '" + value + "' (" + type + "), got: " + current.getValue() + " (" + current.getType() + ") at position " + current.getStartIndex());
        }
        advance();
    }

    private boolean isType(String value) {
        return value != null && (value.equals("int") || value.equals("float") ||
                value.equals("boolean") || value.equals("char") ||
                value.equals("double") || value.equals("long") ||
                value.equals("short") || value.equals("byte") ||
                value.equals("void") || value.equals("String"));
    }

    public boolean checkBalancedBrackets() {
        Deque<TokenType> stack = new ArrayDeque<>();
        for (Token token : tokens) {
            TokenType type = token.getType();
            if (type == TokenType.LPAREN || type == TokenType.LBRACE || type == TokenType.LBRACKET) {
                stack.push(type);
            } else if (type == TokenType.RPAREN || type == TokenType.RBRACE || type == TokenType.RBRACKET) {
                if (stack.isEmpty()) return false;
                TokenType open = stack.pop();
                if (!matches(open, type)) return false;
            }
        }
        return stack.isEmpty();
    }

    private boolean matches(TokenType open, TokenType close) {
        return (open == TokenType.LPAREN && close == TokenType.RPAREN) ||
                (open == TokenType.LBRACE && close == TokenType.RBRACE) ||
                (open == TokenType.LBRACKET && close == TokenType.RBRACKET);
    }

    public List<String> getErrors() {
        return errors;
    }
}

class ParseException extends Exception {
    public ParseException(String message) {
        super(message);
    }
}
