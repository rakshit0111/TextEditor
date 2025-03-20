package com.rakshit.OopsProject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private TextEditor editor;
    private Map<String, Theme> themes;

    public ThemeManager(TextEditor editor) {
        this.editor = editor;
        initializeThemes();
    }

    private void initializeThemes() {
        themes = new HashMap<>();

        // Light theme
        Theme lightTheme = new Theme(
                new Color(255, 255, 255), // background
                new Color(50, 50, 50),    // foreground
                new Color(240, 240, 240), // menuBackground
                new Color(50, 50, 50),    // menuForeground
                new Color(230, 230, 230), // statusBackground
                new Color(50, 50, 50)     // statusForeground
        );
        themes.put("Light", lightTheme);

        // Dark theme
        Theme darkTheme = new Theme(
                new Color(43, 43, 43),    // background
                new Color(220, 220, 220), // foreground
                new Color(60, 60, 60),    // menuBackground
                new Color(220, 220, 220), // menuForeground
                new Color(60, 60, 60),    // statusBackground
                new Color(180, 180, 180)  // statusForeground
        );
        themes.put("Dark", darkTheme);

        // Monokai theme
        Theme monokaiTheme = new Theme(
                new Color(39, 40, 34),    // background
                new Color(248, 248, 242), // foreground
                new Color(39, 40, 34),    // menuBackground
                new Color(248, 248, 242), // menuForeground
                new Color(39, 40, 34),    // statusBackground
                new Color(117, 113, 94)   // statusForeground
        );
        themes.put("Monokai", monokaiTheme);

        // Solarized theme
        Theme solarizedTheme = new Theme(
                new Color(253, 246, 227), // background
                new Color(101, 123, 131), // foreground
                new Color(238, 232, 213), // menuBackground
                new Color(101, 123, 131), // menuForeground
                new Color(238, 232, 213), // statusBackground
                new Color(88, 110, 117)   // statusForeground
        );
        themes.put("Solarized", solarizedTheme);

        // Daisy UI - Cupcake theme
        Theme cupcakeTheme = new Theme(
                new Color(250, 247, 245), // background
                new Color(65, 63, 70),    // foreground
                new Color(241, 229, 231), // menuBackground
                new Color(65, 63, 70),    // menuForeground
                new Color(241, 229, 231), // statusBackground
                new Color(107, 114, 128)  // statusForeground
        );
        themes.put("Cupcake", cupcakeTheme);

        // Daisy UI - Synthwave theme
        Theme synthwaveTheme = new Theme(
                new Color(42, 35, 62),    // background
                new Color(249, 249, 249), // foreground
                new Color(50, 42, 75),    // menuBackground
                new Color(255, 128, 237), // menuForeground
                new Color(50, 42, 75),    // statusBackground
                new Color(230, 103, 206)  // statusForeground
        );
        themes.put("Synthwave", synthwaveTheme);

        // Daisy UI - Dracula theme
        Theme draculaTheme = new Theme(
                new Color(40, 42, 54),    // background
                new Color(248, 248, 242), // foreground
                new Color(40, 42, 54),    // menuBackground
                new Color(248, 248, 242), // menuForeground
                new Color(40, 42, 54),    // statusBackground
                new Color(189, 147, 249)  // statusForeground
        );
        themes.put("Dracula", draculaTheme);

        // Daisy UI - Emerald theme
        Theme emeraldTheme = new Theme(
                new Color(235, 245, 239), // background
                new Color(51, 51, 51),    // foreground
                new Color(218, 234, 225), // menuBackground
                new Color(51, 51, 51),    // menuForeground
                new Color(218, 234, 225), // statusBackground
                new Color(47, 133, 90)    // statusForeground
        );
        themes.put("Emerald", emeraldTheme);

        // Daisy UI - Cyberpunk theme
        Theme cyberpunkTheme = new Theme(
                new Color(27, 26, 32),    // background
                new Color(255, 236, 25),  // foreground
                new Color(27, 26, 32),    // menuBackground
                new Color(255, 236, 25),  // menuForeground
                new Color(27, 26, 32),    // statusBackground
                new Color(255, 0, 110)    // statusForeground
        );
        themes.put("Cyberpunk", cyberpunkTheme);

        // Daisy UI - Retro theme
        Theme retroTheme = new Theme(
                new Color(235, 218, 194), // background
                new Color(44, 45, 45),    // foreground
                new Color(214, 175, 135), // menuBackground
                new Color(44, 45, 45),    // menuForeground
                new Color(214, 175, 135), // statusBackground
                new Color(44, 45, 45)     // statusForeground
        );
        themes.put("Retro", retroTheme);

        // Daisy UI - Valentine theme
        Theme valentineTheme = new Theme(
                new Color(254, 242, 242), // background
                new Color(69, 10, 10),    // foreground
                new Color(253, 226, 226), // menuBackground
                new Color(69, 10, 10),    // menuForeground
                new Color(253, 226, 226), // statusBackground
                new Color(225, 29, 72)    // statusForeground
        );
        themes.put("Valentine", valentineTheme);

        // Daisy UI - Lofi theme
        Theme lofiTheme = new Theme(
                new Color(240, 240, 240), // background
                new Color(28, 28, 28),    // foreground
                new Color(240, 240, 240), // menuBackground
                new Color(28, 28, 28),    // menuForeground
                new Color(240, 240, 240), // statusBackground
                new Color(28, 28, 28)     // statusForeground
        );
        themes.put("Lofi", lofiTheme);

        // Daisy UI - Aqua theme
        Theme aquaTheme = new Theme(
                new Color(207, 241, 241), // background
                new Color(19, 91, 101),   // foreground
                new Color(178, 233, 233), // menuBackground
                new Color(19, 91, 101),   // menuForeground
                new Color(178, 233, 233), // statusBackground
                new Color(19, 91, 101)    // statusForeground
        );
        themes.put("Aqua", aquaTheme);

        // Daisy UI - Coffee theme
        Theme coffeeTheme = new Theme(
                new Color(32, 25, 19),    // background
                new Color(221, 200, 182), // foreground
                new Color(43, 33, 24),    // menuBackground
                new Color(221, 200, 182), // menuForeground
                new Color(43, 33, 24),    // statusBackground
                new Color(176, 145, 120)  // statusForeground
        );
        themes.put("Coffee", coffeeTheme);

        // Daisy UI - Forest theme
        Theme forestTheme = new Theme(
                new Color(30, 30, 17),    // background
                new Color(219, 217, 178), // foreground
                new Color(40, 40, 22),    // menuBackground
                new Color(219, 217, 178), // menuForeground
                new Color(40, 40, 22),    // statusBackground
                new Color(157, 193, 131)  // statusForeground
        );
        themes.put("Forest", forestTheme);
    }

    public void applyTheme(String themeName) {
        Theme theme = themes.get(themeName);
        if (theme == null) {
            return;
        }

        // Apply to text area
        JTextArea textArea = getTextAreaFromEditor();
        if (textArea != null) {
            textArea.setBackground(theme.background);
            textArea.setForeground(theme.foreground);
            textArea.setCaretColor(theme.foreground);
        }

        // Apply to menu bar
        JMenuBar menuBar = editor.getJMenuBar();
        if (menuBar != null) {
            menuBar.setBackground(theme.menuBackground);
            menuBar.setForeground(theme.menuForeground);
            applyThemeToMenuComponents(menuBar, theme);
        }

        // Apply to status bar
        JPanel statusPanel = (JPanel) editor.getContentPane().getComponent(1);
        if (statusPanel != null) {
            statusPanel.setBackground(theme.statusBackground);
            for (Component comp : statusPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    comp.setForeground(theme.statusForeground);
                }
            }
        }

        // Update UI
        SwingUtilities.updateComponentTreeUI(editor);
    }

    private void applyThemeToMenuComponents(Container container, Theme theme) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof JMenu) {
                JMenu menu = (JMenu) component;
                menu.setBackground(theme.menuBackground);
                menu.setForeground(theme.menuForeground);
                applyThemeToMenuComponents(menu.getPopupMenu(), theme);
            } else if (component instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) component;
                menuItem.setBackground(theme.menuBackground);
                menuItem.setForeground(theme.menuForeground);
            } else if (component instanceof Container) {
                applyThemeToMenuComponents((Container) component, theme);
            }
        }
    }

    private JTextArea getTextAreaFromEditor() {
        // Get the text area from the scroll pane
        Container contentPane = editor.getContentPane();
        if (contentPane.getComponentCount() > 0) {
            Component comp = contentPane.getComponent(0);
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component viewComp = scrollPane.getViewport().getView();
                if (viewComp instanceof JTextArea) {
                    return (JTextArea) viewComp;
                }
            }
        }
        return null;
    }

    // Inner class to hold theme colors
    private static class Theme {
        Color background;
        Color foreground;
        Color menuBackground;
        Color menuForeground;
        Color statusBackground;
        Color statusForeground;

        public Theme(Color background, Color foreground,
                     Color menuBackground, Color menuForeground,
                     Color statusBackground, Color statusForeground) {
            this.background = background;
            this.foreground = foreground;
            this.menuBackground = menuBackground;
            this.menuForeground = menuForeground;
            this.statusBackground = statusBackground;
            this.statusForeground = statusForeground;
        }
    }
}