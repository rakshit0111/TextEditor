package com.rakshit.OopsProject;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LineNumberComponent extends JPanel implements CaretListener, DocumentListener {
    private JTextArea textArea;
    private JTextArea lineNumbers;
    private final int MARGIN = 5;

    public LineNumberComponent(JTextArea textArea) {
        this.textArea = textArea;
        setLayout(new BorderLayout());

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(240, 240, 240));
        lineNumbers.setForeground(new Color(100, 100, 100));
        lineNumbers.setEditable(false);
        lineNumbers.setFont(textArea.getFont());
        lineNumbers.setBorder(BorderFactory.createEmptyBorder(0, MARGIN, 0, MARGIN));

        add(lineNumbers, BorderLayout.WEST);
        add(textArea, BorderLayout.CENTER);

        // Add listeners
        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);

        // Update line numbers when component is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLineNumbers();
            }
        });

        updateLineNumbers();
    }

    private void updateLineNumbers() {
        // Get the line count
        Element root = textArea.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();

        // Create the line numbers text
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lineCount; i++) {
            sb.append(i).append("\n");
        }

        // Set the text and adjust width
        lineNumbers.setText(sb.toString());
        int width = getFontMetrics(lineNumbers.getFont()).stringWidth(String.valueOf(lineCount)) + MARGIN * 2;
        lineNumbers.setPreferredSize(new Dimension(width, 0));
    }

    // DocumentListener methods
    @Override
    public void insertUpdate(DocumentEvent e) {
        updateLineNumbers();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateLineNumbers();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateLineNumbers();
    }

    // CaretListener method
    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            // Highlight the current line number
            int caretPos = textArea.getCaretPosition();
            Element root = textArea.getDocument().getDefaultRootElement();
            int currentLine = root.getElementIndex(caretPos) + 1;

            // Update status with line and column information
            int column = caretPos - textArea.getLineStartOffset(currentLine - 1) + 1;

            // This would typically update a status bar with line/column info
            // For now, we'll just highlight the current line number
            lineNumbers.repaint();
        } catch (Exception ex) {
            // Ignore exceptions during caret updates
        }
    }

    // Method to get the current line and column
    public String getCurrentLineColumnInfo() {
        try {
            int caretPos = textArea.getCaretPosition();
            Element root = textArea.getDocument().getDefaultRootElement();
            int currentLine = root.getElementIndex(caretPos) + 1;
            int column = caretPos - textArea.getLineStartOffset(currentLine - 1) + 1;
            return "Line: " + currentLine + " Column: " + column;
        } catch (Exception ex) {
            return "";
        }
    }
}