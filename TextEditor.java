package com.rakshit.OopsProject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TextEditor extends JFrame {
    private JTextArea textArea;
    private JLabel statusLabel;
    private File currentFile = null;
    private boolean textChanged = false;
    private UndoManager undoManager = new UndoManager();
    private ThemeManager themeManager;
    private FontManager fontManager;
    private TypingSpeedTracker typingSpeedTracker;
    private LineNumberComponent lineNumberComponent;
    private AutoSaveManager autoSaveManager;
    private JCheckBoxMenuItem autoSaveMenuItem;
    private JScrollPane scrollPane;

    public TextEditor() {
        // Set up the frame
        setTitle("Java Text Editor");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Create components
        createTextArea();
        createStatusBar();

        // Initialize managers
        themeManager = new ThemeManager(this);
        fontManager = new FontManager(this, textArea);
        typingSpeedTracker = new TypingSpeedTracker(textArea, statusLabel);
        autoSaveManager = new AutoSaveManager(textArea, statusLabel);

        // Add document listener to track changes
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged = true;
                updateTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged = true;
                updateTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged = true;
                updateTitle();
            }
        });

        // Create menu bar
        createMenuBar();

        // Apply default theme
        themeManager.applyTheme("Light");

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        newMenuItem.addActionListener(e -> newFile());
        openMenuItem.addActionListener(e -> openFile());
        saveMenuItem.addActionListener(e -> saveFile());
        saveAsMenuItem.addActionListener(e -> saveFileAs());
        exitMenuItem.addActionListener(e -> exit());

        // Add keyboard shortcuts
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();

        // Auto-save submenu
        JMenu autoSaveMenu = new JMenu("Auto Save");
        autoSaveMenuItem = new JCheckBoxMenuItem("Enable Auto Save");
        autoSaveMenuItem.addActionListener(e -> autoSaveManager.setAutoSaveEnabled(autoSaveMenuItem.isSelected()));

        JMenu intervalMenu = new JMenu("Interval");
        int[] intervals = {30, 60, 120, 300, 600}; // seconds
        for (int interval : intervals) {
            JMenuItem intervalItem = new JMenuItem(formatInterval(interval));
            intervalItem.addActionListener(e -> {
                autoSaveManager.setAutoSaveInterval(interval);
                if (autoSaveManager.isAutoSaveEnabled()) {
                    statusLabel.setText("Auto-save interval set to " + formatInterval(interval));
                }
            });
            intervalMenu.add(intervalItem);
        }

        autoSaveMenu.add(autoSaveMenuItem);
        autoSaveMenu.add(intervalMenu);
        fileMenu.add(autoSaveMenu);
        fileMenu.addSeparator();

        fileMenu.add(exitMenuItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        JMenuItem cutMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        JMenuItem copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        JMenuItem pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        JMenuItem findReplaceMenuItem = new JMenuItem("Find/Replace");

        undoMenuItem.addActionListener(e -> undo());
        redoMenuItem.addActionListener(e -> redo());
        cutMenuItem.setText("Cut");
        copyMenuItem.setText("Copy");
        pasteMenuItem.setText("Paste");
        selectAllMenuItem.addActionListener(e -> textArea.selectAll());
        findReplaceMenuItem.addActionListener(e -> showFindReplaceDialog());

        // Add keyboard shortcuts
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        findReplaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));

        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);
        editMenu.addSeparator();
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.addSeparator();
        editMenu.add(selectAllMenuItem);
        editMenu.addSeparator();
        editMenu.add(findReplaceMenuItem);

        // View menu
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem wordWrapMenuItem = new JCheckBoxMenuItem("Word Wrap", true);
        JCheckBoxMenuItem lineNumbersMenuItem = new JCheckBoxMenuItem("Line Numbers", true);
        JMenuItem focusModeMenuItem = new JMenuItem("Focus Mode");

        wordWrapMenuItem.addActionListener(e -> {
            textArea.setLineWrap(wordWrapMenuItem.isSelected());
            textArea.setWrapStyleWord(wordWrapMenuItem.isSelected());
        });

        lineNumbersMenuItem.addActionListener(e -> {
            if (lineNumbersMenuItem.isSelected()) {
                // Show line numbers
                if (scrollPane.getRowHeader() == null) {
                    JTextArea lineNumberArea = new JTextArea("1");
                    lineNumberArea.setBackground(new Color(240, 240, 240));
                    lineNumberArea.setForeground(new Color(100, 100, 100));
                    lineNumberArea.setEditable(false);
                    lineNumberArea.setFont(textArea.getFont());
                    lineNumberArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

                    // Calculate width based on number of lines
                    int lineCount = textArea.getLineCount();
                    int width = getFontMetrics(lineNumberArea.getFont()).stringWidth(String.valueOf(lineCount)) + 10;
                    lineNumberArea.setPreferredSize(new Dimension(width, 0));

                    scrollPane.setRowHeaderView(lineNumberArea);

                    // Update line numbers when text changes
                    textArea.getDocument().addDocumentListener(new DocumentListener() {
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

                        private void updateLineNumbers() {
                            int lineCount = textArea.getLineCount();
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i <= lineCount; i++) {
                                sb.append(i).append("\n");
                            }
                            lineNumberArea.setText(sb.toString());

                            // Adjust width if needed
                            int width = getFontMetrics(lineNumberArea.getFont()).stringWidth(String.valueOf(lineCount)) + 10;
                            if (width != lineNumberArea.getPreferredSize().width) {
                                lineNumberArea.setPreferredSize(new Dimension(width, 0));
                                scrollPane.revalidate();
                            }
                        }
                    });
                }
            } else {
                // Hide line numbers
                scrollPane.setRowHeaderView(null);
            }
        });

        focusModeMenuItem.addActionListener(e -> toggleFocusMode());

        viewMenu.add(wordWrapMenuItem);
        viewMenu.add(lineNumbersMenuItem);
        viewMenu.add(focusModeMenuItem);

        // Format menu
        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontMenuItem = new JMenuItem("Font...");

        fontMenuItem.addActionListener(e -> fontManager.showFontDialog());

        formatMenu.add(fontMenuItem);

        // Theme menu
        JMenu themeMenu = new JMenu("Theme");
        String[] themes = {
                "Light", "Dark", "Monokai", "Solarized",
                "Cupcake", "Synthwave", "Dracula", "Emerald",
                "Cyberpunk", "Retro", "Valentine", "Lofi",
                "Aqua", "Coffee", "Forest"
        };

        for (String theme : themes) {
            JMenuItem themeItem = new JMenuItem(theme);
            themeItem.addActionListener(e -> themeManager.applyTheme(theme));
            themeMenu.add(themeItem);
        }

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        JMenuItem shortcutsMenuItem = new JMenuItem("Keyboard Shortcuts");

        aboutMenuItem.addActionListener(e -> showAboutDialog());
        shortcutsMenuItem.addActionListener(e -> showShortcutsDialog());

        helpMenu.add(aboutMenuItem);
        helpMenu.add(shortcutsMenuItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(formatMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private String formatInterval(int seconds) {
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes";
        } else {
            return (seconds / 3600) + " hours";
        }
    }

    private void createTextArea() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Add undo manager
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // Add key listeners for typing speed tracking
        textArea.addKeyListener(typingSpeedTracker);

        // Create scroll pane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Add to frame
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(2, 5, 2, 5));

        statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Add cursor position label
        JLabel positionLabel = new JLabel("Line: 1 Column: 1");
        statusPanel.add(positionLabel, BorderLayout.EAST);

        // Update position label when caret moves
        textArea.addCaretListener(e -> {
            try {
                int caretPos = textArea.getCaretPosition();
                int lineNum = textArea.getLineOfOffset(caretPos);
                int columnNum = caretPos - textArea.getLineStartOffset(lineNum);
                positionLabel.setText("Line: " + (lineNum + 1) + " Column: " + (columnNum + 1));
            } catch (Exception ex) {
                positionLabel.setText("Line: ? Column: ?");
            }
        });

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void newFile() {
        if (checkUnsavedChanges()) {
            textArea.setText("");
            currentFile = null;
            textChanged = false;
            updateTitle();
            statusLabel.setText("New file created");
            typingSpeedTracker.reset();
            autoSaveManager.setCurrentFile(null);
        }
    }

    private void openFile() {
        if (checkUnsavedChanges()) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                    textArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                    textChanged = false;
                    updateTitle();
                    statusLabel.setText("Opened: " + currentFile.getName());
                    typingSpeedTracker.reset();
                    autoSaveManager.setCurrentFile(currentFile);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private boolean saveFile() {
        if (currentFile == null) {
            return saveFileAs();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(textArea.getText());
                textChanged = false;
                updateTitle();
                statusLabel.setText("Saved: " + currentFile.getName());
                autoSaveManager.setCurrentFile(currentFile);
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    private boolean saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            return saveFile();
        }
        return false;
    }

    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }

    private void showFindReplaceDialog() {
        FindReplaceDialog dialog = new FindReplaceDialog(this, textArea);
        dialog.setVisible(true);
    }

    private void toggleFocusMode() {
        // Create a new frame for focus mode
        JFrame focusFrame = new JFrame("Focus Mode - Java Text Editor");
        focusFrame.setSize(800, 600);
        focusFrame.setLocationRelativeTo(null);

        // Create a new text area with the same content
        JTextArea focusTextArea = new JTextArea(textArea.getText());
        focusTextArea.setFont(textArea.getFont());
        focusTextArea.setLineWrap(true);
        focusTextArea.setWrapStyleWord(true);
        focusTextArea.setBackground(new Color(250, 250, 245));
        focusTextArea.setForeground(new Color(50, 50, 50));
        focusTextArea.setCaretColor(new Color(100, 100, 100));

        // Add a border for better readability
        JScrollPane focusScrollPane = new JScrollPane(focusTextArea);
        focusScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        focusScrollPane.setBackground(new Color(250, 250, 245));

        // Sync content between text areas
        focusTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textArea.setText(focusTextArea.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textArea.setText(focusTextArea.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textArea.setText(focusTextArea.getText());
            }
        });

        // Add a simple toolbar with exit button
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setBackground(new Color(240, 240, 235));
        JButton exitFocusButton = new JButton("Exit Focus Mode");
        exitFocusButton.addActionListener(e -> {
            focusFrame.dispose();
            textArea.requestFocus();
        });
        toolbar.add(exitFocusButton);

        focusFrame.add(toolbar, BorderLayout.NORTH);
        focusFrame.add(focusScrollPane, BorderLayout.CENTER);

        // Add keyboard shortcut to exit focus mode (Escape)
        focusTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitFocus");
        focusTextArea.getActionMap().put("exitFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusFrame.dispose();
                textArea.requestFocus();
            }
        });

        focusFrame.setVisible(true);
        focusTextArea.requestFocus();
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Java Text Editor\nVersion 1.0\n\n" +
                        "A feature-rich text editor with themes, font selection,\n" +
                        "typing speed tracking, and more.\n\n" +
                        "© 2025",
                "About Java Text Editor",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showShortcutsDialog() {
        String shortcuts =
                "Keyboard Shortcuts:\n\n" +
                        "File Menu:\n" +
                        "Ctrl+N: New File\n" +
                        "Ctrl+O: Open File\n" +
                        "Ctrl+S: Save\n" +
                        "Ctrl+Shift+S: Save As\n\n" +
                        "Edit Menu:\n" +
                        "Ctrl+Z: Undo\n" +
                        "Ctrl+Y: Redo\n" +
                        "Ctrl+X: Cut\n" +
                        "Ctrl+C: Copy\n" +
                        "Ctrl+V: Paste\n" +
                        "Ctrl+A: Select All\n" +
                        "Ctrl+F: Find/Replace\n\n" +
                        "Focus Mode:\n" +
                        "Esc: Exit Focus Mode";

        JOptionPane.showMessageDialog(this,
                shortcuts,
                "Keyboard Shortcuts",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exit() {
        if (checkUnsavedChanges()) {
            // Perform final auto-save if enabled
            if (autoSaveManager.isAutoSaveEnabled()) {
                autoSaveManager.shutdown();
            }

            dispose();
            System.exit(0);
        }
    }

    private boolean checkUnsavedChanges() {
        if (textChanged) {
            int option = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Do you want to save before continuing?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                return saveFile();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    private void updateTitle() {
        String title = "Java Text Editor";
        if (currentFile != null) {
            title = currentFile.getName() + " - " + title;
        }
        if (textChanged) {
            title = "*" + title;
        }
        setTitle(title);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TextEditor());
    }
}