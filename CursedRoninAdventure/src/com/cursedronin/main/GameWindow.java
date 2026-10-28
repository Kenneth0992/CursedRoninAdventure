package com.cursedronin.main;

import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameWindow {
    private JFrame window;
    private boolean isFullScreen = false;

    public GameWindow(GamePanel gamePanel) {
        window = new JFrame("Cursed Ronin Adventure");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);

        // Add a global key listener to the window for Full-Screen Toggle (F11)
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    toggleFullScreen(gamePanel);
                }
            }
        });

        window.setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    private void toggleFullScreen(GamePanel gamePanel) {
        // A JFrame must be disposed or hidden briefly when changing decoration state
        window.dispose();
        
        isFullScreen = !isFullScreen;

        if (isFullScreen) {
            // Switch to Borderless Full-Screen Mode
            window.setUndecorated(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            // Switch back to Windowed Mode
            window.setUndecorated(false);
            window.setExtendedState(JFrame.NORMAL);
            window.pack();
            window.setLocationRelativeTo(null);
        }

        window.setVisible(true);
        gamePanel.requestFocusInWindow(); // Ensure keyboard focus returns to the game canvas
    }
}