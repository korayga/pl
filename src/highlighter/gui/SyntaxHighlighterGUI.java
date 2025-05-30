package highlighter.gui;

import highlighter.core.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.*;

public class SyntaxHighlighterGUI {
    private static final int DEBOUNCE_MS = 50;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> pendingUpdate;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java Syntax Highlighter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            JTextPane textPane = new JTextPane();
            textPane.setFont(new Font("Consolas", Font.PLAIN, 16));
            textPane.setBackground(new Color(30, 30, 30));
            textPane.setForeground(Color.WHITE);
            textPane.setCaretColor(Color.WHITE);
            textPane.setSelectionColor(new Color(56, 143, 255));

            StyleContext styleContext = new StyleContext();
            StyledDocument doc = textPane.getStyledDocument();
            Style defaultStyle = styleContext.addStyle("default", null);
            StyleConstants.setForeground(defaultStyle, Color.WHITE);

            JTextArea lineNumbers = new JTextArea("1\n");
            lineNumbers.setFont(new Font("Consolas", Font.PLAIN, 16));
            lineNumbers.setBackground(new Color(40, 40, 40));
            lineNumbers.setForeground(Color.LIGHT_GRAY);
            lineNumbers.setEditable(false);

            textPane.getDocument().addDocumentListener(new DocumentListener() {
                private void updateLineNumbers() {
                    String text = textPane.getText();
                    int lineCount = text.isEmpty() ? 1 : text.split("\n", -1).length;
                    StringBuilder numbers = new StringBuilder();
                    for (int i = 1; i <= lineCount; i++) {
                        numbers.append(i).append("\n");
                    }
                    lineNumbers.setText(numbers.toString());
                }

                public void insertUpdate(DocumentEvent e) { updateLineNumbers(); }
                public void removeUpdate(DocumentEvent e) { updateLineNumbers(); }
                public void changedUpdate(DocumentEvent e) {}
            });

            JPanel panel = new JPanel(new BorderLayout());
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setRowHeaderView(lineNumbers);
            panel.add(scrollPane, BorderLayout.CENTER);
            frame.add(panel);

            textPane.getDocument().addDocumentListener(new DocumentListener() {
                private void scheduleUpdate() {
                    if (pendingUpdate != null) {
                        pendingUpdate.cancel(false);
                    }
                    pendingUpdate = scheduler.schedule(() -> SwingUtilities.invokeLater(() -> {
                        try {
                            String code = textPane.getText();
                            Lexer lexer = new Lexer();
                            List<Token> tokens = lexer.tokenize(code);

                            doc.removeDocumentListener(this);
                            doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

                            for (Token token : tokens) {
                                if (token.getType() == TokenType.EOF) continue;

                                int startIndex = token.getStartIndex();
                                int endIndex = token.getEndIndex();

                                if (startIndex >= 0 && startIndex < doc.getLength() &&
                                        endIndex >= startIndex && endIndex < doc.getLength()) {

                                    Color color = HighlightPainter.getColor(token.getType());
                                    Style style = styleContext.addStyle("token_" + System.nanoTime(), null);
                                    StyleConstants.setForeground(style, color);

                                    int length = endIndex - startIndex + 1;
                                    if (startIndex + length <= doc.getLength()) {
                                        doc.setCharacterAttributes(startIndex, length, style, false);
                                    }
                                }
                            }

                        } catch (LexicalException e) {
                            System.err.println("Sözdizimi hatası: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Hata: " + e.getMessage());
                        } finally {
                            doc.addDocumentListener(this);
                        }
                    }), DEBOUNCE_MS, TimeUnit.MILLISECONDS);
                }

                public void insertUpdate(DocumentEvent e) { scheduleUpdate(); }
                public void removeUpdate(DocumentEvent e) { scheduleUpdate(); }
                public void changedUpdate(DocumentEvent e) {}
            });

            SwingUtilities.invokeLater(() -> {
                String testCode = "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "       System.out.println(\"Try korayga highlighter\");\n" +
                        "    \n" +
                        "   }\n" +
                        "}";
                textPane.setText(testCode);
            });

            frame.setVisible(true);
        });
    }
}