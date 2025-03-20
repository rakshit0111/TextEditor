package com.rakshit.OopsProject;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AutoSaveManager {
    private JTextArea textArea;
    private Timer autoSaveTimer;
    private File currentFile;
    private JLabel statusLabel;
    private boolean autoSaveEnabled = false;
    private int autoSaveInterval = 60; // seconds
    private Path autoSaveDirectory;

    public AutoSaveManager(JTextArea textArea, JLabel statusLabel) {
        this.textArea = textArea;
        this.statusLabel = statusLabel;

        // Create auto-save directory in user's home directory
        try {
            autoSaveDirectory = Paths.get(System.getProperty("user.home"), ".texteditor", "autosave");
            Files.createDirectories(autoSaveDirectory);
        } catch (IOException e) {
            System.err.println("Failed to create auto-save directory: " + e.getMessage());
        }

        // Initialize timer but don't start it yet
        autoSaveTimer = new Timer(autoSaveInterval * 1000, e -> performAutoSave());
        autoSaveTimer.setRepeats(true);
    }

    public void setCurrentFile(File file) {
        this.currentFile = file;
    }

    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
        if (enabled) {
            autoSaveTimer.start();
            statusLabel.setText("Auto-save enabled (" + autoSaveInterval + "s)");
        } else {
            autoSaveTimer.stop();
            statusLabel.setText("Auto-save disabled");
        }
    }

    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    public void setAutoSaveInterval(int seconds) {
        this.autoSaveInterval = seconds;
        autoSaveTimer.setDelay(seconds * 1000);
        if (autoSaveEnabled) {
            statusLabel.setText("Auto-save enabled (" + autoSaveInterval + "s)");
        }
    }

    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }

    private void performAutoSave() {
        if (!autoSaveEnabled || textArea.getText().isEmpty()) {
            return;
        }

        try {
            // If we have a current file, save to it directly
            if (currentFile != null) {
                saveToFile(currentFile);
                statusLabel.setText("Auto-saved to " + currentFile.getName());
                return;
            }

            // Otherwise save to auto-save directory with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File autoSaveFile = new File(autoSaveDirectory.toFile(), "autosave_" + timestamp + ".txt");
            saveToFile(autoSaveFile);
            statusLabel.setText("Auto-saved to " + autoSaveFile.getName());
        } catch (IOException e) {
            statusLabel.setText("Auto-save failed: " + e.getMessage());
        }
    }

    private void saveToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
        }
    }

    public void shutdown() {
        autoSaveTimer.stop();
        performAutoSave(); // Do a final save on shutdown
    }
}
