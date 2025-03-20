package com.rakshit.OopsProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FontManager {
    private TextEditor editor;
    private JTextArea textArea;
    private List<String> popularFonts;

    public FontManager(TextEditor editor, JTextArea textArea) {
        this.editor = editor;
        this.textArea = textArea;
        initializePopularFonts();
    }

    private void initializePopularFonts() {
        // List of popular programming and writing fonts
        popularFonts = new ArrayList<>(Arrays.asList(
                "Monospaced", "Consolas", "Courier New", "DejaVu Sans Mono",
                "Lucida Console", "Source Code Pro", "Ubuntu Mono", "Fira Code",
                "JetBrains Mono", "Roboto Mono", "Inconsolata", "Anonymous Pro",
                "Liberation Mono", "PT Mono", "IBM Plex Mono"
        ));

        // Filter to only include fonts available on the system
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        List<String> availableFontsList = Arrays.asList(availableFonts);

        popularFonts.removeIf(font -> !availableFontsList.contains(font));

        // Always keep Monospaced as it's guaranteed to be available
        if (!popularFonts.contains("Monospaced")) {
            popularFonts.add(0, "Monospaced");
        }
    }

    public List<String> getAvailableFonts() {
        return popularFonts;
    }

    public void setFont(String fontName, int style, int size) {
        Font newFont = new Font(fontName, style, size);
        textArea.setFont(newFont);
    }

    public void showFontDialog() {
        JDialog fontDialog = new JDialog(editor, "Font Settings", true);
        fontDialog.setLayout(new BorderLayout());
        fontDialog.setSize(400, 300);
        fontDialog.setLocationRelativeTo(editor);

        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Font family selection
        JPanel fontFamilyPanel = new JPanel(new BorderLayout());
        fontFamilyPanel.add(new JLabel("Font Family:"), BorderLayout.NORTH);
        JComboBox<String> fontFamilyCombo = new JComboBox<>(popularFonts.toArray(new String[0]));
        fontFamilyPanel.add(fontFamilyCombo, BorderLayout.CENTER);

        // Font style selection
        JPanel fontStylePanel = new JPanel(new BorderLayout());
        fontStylePanel.add(new JLabel("Font Style:"), BorderLayout.NORTH);
        String[] styles = {"Regular", "Bold", "Italic", "Bold Italic"};
        JComboBox<String> fontStyleCombo = new JComboBox<>(styles);
        fontStylePanel.add(fontStyleCombo, BorderLayout.CENTER);

        // Font size selection
        JPanel fontSizePanel = new JPanel(new BorderLayout());
        fontSizePanel.add(new JLabel("Font Size:"), BorderLayout.NORTH);
        Integer[] sizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
        JComboBox<Integer> fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(textArea.getFont().getSize());
        fontSizePanel.add(fontSizeCombo, BorderLayout.CENTER);

        // Preview panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(new JLabel("Preview:"), BorderLayout.NORTH);
        JTextArea previewArea = new JTextArea("AaBbCcXxYyZz 123456789");
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setBorder(BorderFactory.createEtchedBorder());
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        // Set initial values based on current font
        Font currentFont = textArea.getFont();
        fontFamilyCombo.setSelectedItem(currentFont.getFamily());
        fontStyleCombo.setSelectedIndex(currentFont.getStyle());
        fontSizeCombo.setSelectedItem(currentFont.getSize());
        previewArea.setFont(currentFont);

        // Add listeners to update preview
        fontFamilyCombo.addActionListener(e -> updatePreviewFont(
                fontFamilyCombo, fontStyleCombo, fontSizeCombo, previewArea));
        fontStyleCombo.addActionListener(e -> updatePreviewFont(
                fontFamilyCombo, fontStyleCombo, fontSizeCombo, previewArea));
        fontSizeCombo.addActionListener(e -> updatePreviewFont(
                fontFamilyCombo, fontStyleCombo, fontSizeCombo, previewArea));

        // Add panels to main panel
        mainPanel.add(fontFamilyPanel);
        mainPanel.add(fontStylePanel);
        mainPanel.add(fontSizePanel);
        mainPanel.add(previewPanel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            String family = (String) fontFamilyCombo.getSelectedItem();
            int style = fontStyleCombo.getSelectedIndex();
            int size = (Integer) fontSizeCombo.getSelectedItem();
            setFont(family, style, size);
            fontDialog.dispose();
        });

        cancelButton.addActionListener(e -> fontDialog.dispose());

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        fontDialog.add(mainPanel, BorderLayout.CENTER);
        fontDialog.add(buttonsPanel, BorderLayout.SOUTH);
        fontDialog.setVisible(true);
    }

    private void updatePreviewFont(JComboBox<String> fontFamilyCombo,
                                   JComboBox<String> fontStyleCombo,
                                   JComboBox<Integer> fontSizeCombo,
                                   JTextArea previewArea) {
        String family = (String) fontFamilyCombo.getSelectedItem();
        int style = fontStyleCombo.getSelectedIndex();
        int size = (Integer) fontSizeCombo.getSelectedItem();
        Font font = new Font(family, style, size);
        previewArea.setFont(font);
    }
}