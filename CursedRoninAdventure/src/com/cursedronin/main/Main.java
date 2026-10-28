package com.cursedronin.main;

public class Main {
    public static void main(String[] args) {
        GamePanel gamePanel = new GamePanel();
        new GameWindow(gamePanel);
        gamePanel.startGameThread();
    }
}