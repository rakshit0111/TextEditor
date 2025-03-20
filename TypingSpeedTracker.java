package com.rakshit.OopsProject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TypingSpeedTracker implements DocumentListener, KeyListener {
    private JTextArea textArea;
    private JLabel statusLabel;

    private Instant startTime;
    private Instant lastKeyTime;
    private int totalKeystrokes;
    private int wordCount;
    private boolean isTracking;
    private List<Integer> recentWPMs;
    private Timer updateTimer;

    // Constants
    private static final int IDLE_RESET_SECONDS = 3;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int WPM_AVERAGE_SAMPLES = 5;
    private static final double AVERAGE_WORD_LENGTH = 5.0; // Average word length in keystrokes

    public TypingSpeedTracker(JTextArea textArea, JLabel statusLabel) {
        this.textArea = textArea;
        this.statusLabel = statusLabel;
        this.recentWPMs = new ArrayList<>();

        reset();

        // Create timer to update WPM display
        updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateWPM());
        updateTimer.start();
    }

    public void reset() {
        startTime = Instant.now();
        lastKeyTime = startTime;
        totalKeystrokes = 0;
        wordCount = 0;
        isTracking = false;
        recentWPMs.clear();
    }

    private void updateWPM() {
        if (!isTracking) {
            return;
        }

        // Check if user has been idle
        Duration idleTime = Duration.between(lastKeyTime, Instant.now());
        if (idleTime.getSeconds() > IDLE_RESET_SECONDS) {
            // User is idle, don't update
            return;
        }

        // Calculate current WPM
        Duration typingDuration = Duration.between(startTime, Instant.now());
        double minutes = typingDuration.toMillis() / 60000.0;

        // Calculate WPM based on keystrokes (5 keystrokes = 1 word on average)
        int wpm = minutes > 0 ? (int) (totalKeystrokes / AVERAGE_WORD_LENGTH / minutes) : 0;

        // Add to recent WPMs for averaging
        recentWPMs.add(wpm);
        if (recentWPMs.size() > WPM_AVERAGE_SAMPLES) {
            recentWPMs.remove(0);
        }

        // Calculate average WPM
        int avgWpm = recentWPMs.stream().mapToInt(Integer::intValue).sum() / recentWPMs.size();

        // Update status label with WPM
        updateStatusLabel(avgWpm);
    }

    private void updateStatusLabel(int wpm) {
        // Count words in text
        String text = textArea.getText();
        String[] words = text.split("\\s+");
        int wordCount = words.length;

        // Count characters
        int charCount = text.length();

        statusLabel.setText(String.format("WPM: %d | Words: %d | Characters: %d", wpm, wordCount, charCount));
    }

    // DocumentListener methods
    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!isTracking) {
            isTracking = true;
            startTime = Instant.now();
        }
        lastKeyTime = Instant.now();
        totalKeystrokes += e.getLength();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        lastKeyTime = Instant.now();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Plain text components don't fire these events
    }

    // KeyListener methods
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isTracking) {
            isTracking = true;
            startTime = Instant.now();
        }
        lastKeyTime = Instant.now();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    public void stopTracking() {
        updateTimer.stop();
    }

    public void resumeTracking() {
        updateTimer.start();
    }
}
