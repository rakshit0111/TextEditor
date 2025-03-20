package com.rakshit.OopsProject;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FindReplaceDialog extends JDialog {
    private JTextArea textArea;
    private JTextField findField;
    private JTextField replaceField;
    private JCheckBox matchCaseCheckBox;
    private JCheckBox wholeWordCheckBox;
    private JCheckBox regexCheckBox;
    private JLabel statusLabel;

    private List<Integer> foundPositions;
    private int currentPosition = -1;
    private Highlighter.HighlightPainter highlightPainter;

    public FindReplaceDialog(JFrame parent, JTextArea textArea) {
        super(parent, "Find and Replace", false);
        this.textArea = textArea;
        this.foundPositions = new ArrayList<>();
        this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 0, 128));

        initComponents();

        setSize(400, 250);
        setLocationRelativeTo(parent);

        // Clear highlights when dialog is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clearHighlights();
            }
        });
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input fields panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Find:"));
        findField = new JTextField(20);
        inputPanel.add(findField);
        inputPanel.add(new JLabel("Replace with:"));
        replaceField = new JTextField(20);
        inputPanel.add(replaceField);

        // Options panel
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        matchCaseCheckBox = new JCheckBox("Match case");
        wholeWordCheckBox = new JCheckBox("Whole word");
        regexCheckBox = new JCheckBox("Regular expression");
        optionsPanel.add(matchCaseCheckBox);
        optionsPanel.add(wholeWordCheckBox);
        optionsPanel.add(regexCheckBox);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton findButton = new JButton("Find Next");
        JButton findAllButton = new JButton("Find All");
        JButton replaceButton = new JButton("Replace");
        JButton replaceAllButton = new JButton("Replace All");
        JButton closeButton = new JButton("Close");

        findButton.addActionListener(e -> findNext());
        findAllButton.addActionListener(e -> findAll());
        replaceButton.addActionListener(e -> replace());
        replaceAllButton.addActionListener(e -> replaceAll());
        closeButton.addActionListener(e -> {
            clearHighlights();
            dispose();
        });

        buttonsPanel.add(findButton);
        buttonsPanel.add(findAllButton);
        buttonsPanel.add(replaceButton);
        buttonsPanel.add(replaceAllButton);
        buttonsPanel.add(closeButton);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());

        // Add components to main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(optionsPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void findNext() {
        String searchText = findField.getText();
        if (searchText.isEmpty()) {
            statusLabel.setText("Please enter text to search");
            return;
        }

        clearHighlights();

        String text = textArea.getText();
        int startIndex = textArea.getCaretPosition();

        // If we're at the end of a previous match, move forward one character
        if (currentPosition != -1 && startIndex == currentPosition + searchText.length()) {
            startIndex++;
        }

        // Wrap around if we're at the end
        if (startIndex >= text.length()) {
            startIndex = 0;
        }

        int foundIndex = findInText(text, searchText, startIndex);

        if (foundIndex != -1) {
            highlightText(foundIndex, searchText.length());
            textArea.setCaretPosition(foundIndex + searchText.length());
            currentPosition = foundIndex;
            statusLabel.setText("Found match");
        } else {
            // Try from the beginning if not found
            if (startIndex > 0) {
                foundIndex = findInText(text, searchText, 0);
                if (foundIndex != -1) {
                    highlightText(foundIndex, searchText.length());
                    textArea.setCaretPosition(foundIndex + searchText.length());
                    currentPosition = foundIndex;
                    statusLabel.setText("Found match (wrapped search)");
                } else {
                    statusLabel.setText("No matches found");
                }
            } else {
                statusLabel.setText("No matches found");
            }
        }
    }

    private void findAll() {
        String searchText = findField.getText();
        if (searchText.isEmpty()) {
            statusLabel.setText("Please enter text to search");
            return;
        }

        clearHighlights();
        foundPositions.clear();

        String text = textArea.getText();
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = findInText(text, searchText, lastIndex);

            if (lastIndex != -1) {
                foundPositions.add(lastIndex);
                highlightText(lastIndex, searchText.length());
                lastIndex += searchText.length();
                count++;
            }
        }

        if (count > 0) {
            statusLabel.setText("Found " + count + " matches");
        } else {
            statusLabel.setText("No matches found");
        }
    }

    private void replace() {
        String searchText = findField.getText();
        String replaceText = replaceField.getText();

        if (searchText.isEmpty()) {
            statusLabel.setText("Please enter text to search");
            return;
        }

        if (currentPosition != -1) {
            try {
                textArea.getDocument().remove(currentPosition, searchText.length());
                textArea.getDocument().insertString(currentPosition, replaceText, null);
                clearHighlights();
                currentPosition = -1;
                findNext(); // Find the next occurrence
            } catch (BadLocationException e) {
                statusLabel.setText("Error during replace: " + e.getMessage());
            }
        } else {
            findNext(); // Find first if none selected
        }
    }

    private void replaceAll() {
        String searchText = findField.getText();
        String replaceText = replaceField.getText();

        if (searchText.isEmpty()) {
            statusLabel.setText("Please enter text to search");
            return;
        }

        clearHighlights();

        String text = textArea.getText();

        if (regexCheckBox.isSelected()) {
            try {
                Pattern pattern = createSearchPattern(searchText);
                Matcher matcher = pattern.matcher(text);
                text = matcher.replaceAll(replaceText);
                textArea.setText(text);
                statusLabel.setText("Replaced all occurrences");
            } catch (PatternSyntaxException e) {
                statusLabel.setText("Invalid regular expression: " + e.getMessage());
            }
        } else {
            // Simple replace all for non-regex
            StringBuilder sb = new StringBuilder(text);
            int lastIndex = 0;
            int count = 0;

            while (lastIndex != -1) {
                lastIndex = findInText(sb.toString(), searchText, lastIndex);

                if (lastIndex != -1) {
                    sb.replace(lastIndex, lastIndex + searchText.length(), replaceText);
                    lastIndex += replaceText.length();
                    count++;
                }
            }

            textArea.setText(sb.toString());
            statusLabel.setText("Replaced " + count + " occurrences");
        }
    }

    private int findInText(String text, String searchText, int startIndex) {
        if (regexCheckBox.isSelected()) {
            try {
                Pattern pattern = createSearchPattern(searchText);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find(startIndex)) {
                    return matcher.start();
                }
            } catch (PatternSyntaxException e) {
                statusLabel.setText("Invalid regular expression: " + e.getMessage());
            }
            return -1;
        } else {
            // Simple text search
            if (matchCaseCheckBox.isSelected()) {
                if (wholeWordCheckBox.isSelected()) {
                    // Whole word, case sensitive
                    return findWholeWord(text, searchText, startIndex, true);
                } else {
                    // Case sensitive
                    return text.indexOf(searchText, startIndex);
                }
            } else {
                if (wholeWordCheckBox.isSelected()) {
                    // Whole word, case insensitive
                    return findWholeWord(text, searchText, startIndex, false);
                } else {
                    // Case insensitive
                    return text.toLowerCase().indexOf(searchText.toLowerCase(), startIndex);
                }
            }
        }
    }

    private int findWholeWord(String text, String word, int startIndex, boolean matchCase) {
        String textToSearch = matchCase ? text : text.toLowerCase();
        String wordToFind = matchCase ? word : word.toLowerCase();

        int index = textToSearch.indexOf(wordToFind, startIndex);
        while (index != -1) {
            // Check if the found occurrence is a whole word
            boolean startIsBoundary = index == 0 || !Character.isLetterOrDigit(text.charAt(index - 1));
            boolean endIsBoundary = index + word.length() >= text.length() ||
                    !Character.isLetterOrDigit(text.charAt(index + word.length()));

            if (startIsBoundary && endIsBoundary) {
                return index;
            }

            // Continue searching
            index = textToSearch.indexOf(wordToFind, index + 1);
        }

        return -1;
    }

    private Pattern createSearchPattern(String searchText) {
        int flags = matchCaseCheckBox.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;

        if (wholeWordCheckBox.isSelected()) {
            searchText = "\\b" + searchText + "\\b";
        }

        return Pattern.compile(searchText, flags);
    }

    private void highlightText(int start, int length) {
        try {
            textArea.getHighlighter().addHighlight(start, start + length, highlightPainter);
        } catch (BadLocationException e) {
            statusLabel.setText("Error highlighting text: " + e.getMessage());
        }
    }

    private void clearHighlights() {
        textArea.getHighlighter().removeAllHighlights();
    }
}
