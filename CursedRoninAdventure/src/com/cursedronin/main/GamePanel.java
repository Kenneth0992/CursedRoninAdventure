package com.cursedronin.main;

import com.cursedronin.inputs.KeyHandler;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16; 
    final int scale = 3; 
    public final int tileSize = originalTileSize * scale; // 48x48 base tile size
    
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; 
    final int screenHeight = tileSize * maxScreenRow; 

    int FPS = 60;
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    int playerX = 200;
    int playerY = 200;
    int playerSpeed = 4;

    // Animation and Direction Tracking
    BufferedImage[] runFrames = new BufferedImage[4];
    BufferedImage idleImage;
    int spriteCounter = 0;
    int spriteNum = 0;
    boolean isMoving = false;
    boolean facingLeft = false; // Tracks current look direction (default false = facing right)

    // Background Asset
    BufferedImage backgroundImg;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
        loadGameAssets();
    }

    public void loadGameAssets() {
        try {
            // Load background room image
            backgroundImg = ImageIO.read(getClass().getResourceAsStream("/images/room_background.jfif"));
            
            // Load individual running frames
            runFrames[0] = ImageIO.read(getClass().getResourceAsStream("/images/kurai1.png"));
            runFrames[1] = ImageIO.read(getClass().getResourceAsStream("/images/kurai2.png"));
            runFrames[2] = ImageIO.read(getClass().getResourceAsStream("/images/kurai3.png"));
            runFrames[3] = ImageIO.read(getClass().getResourceAsStream("/images/kurai4.png"));
            
            idleImage = runFrames[0];

            if (backgroundImg == null || runFrames[0] == null || runFrames[1] == null || 
                runFrames[2] == null || runFrames[3] == null) {
                throw new IOException("One or more Kurai frame assets could not be found in /res/images/!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        int dx = 0, dy = 0;
        isMoving = false;
        
        // Check 8-directional inputs
        if (keyH.up) { dy -= playerSpeed; isMoving = true; }
        if (keyH.down) { dy += playerSpeed; isMoving = true; }
        
        if (keyH.left) { 
            dx -= playerSpeed; 
            isMoving = true; 
            facingLeft = true; // Flip sprite to face left
        }
        if (keyH.right) { 
            dx += playerSpeed; 
            isMoving = true; 
            facingLeft = false; // Face right (default)
        }

        // Normalize diagonal speed
        if (dx != 0 && dy != 0) {
            playerX += (int)(dx * 0.7071);
            playerY += (int)(dy * 0.7071);
        } else {
            playerX += dx;
            playerY += dy;
        }

        // Cycle animation frames if moving
        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum + 1) % 4;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0; 
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Render Background Floor
        if (backgroundImg != null) {
            g2.drawImage(backgroundImg, 0, 0, screenWidth, screenHeight, null);
        }

        // Select correct frame based on movement state
        BufferedImage currentImage = isMoving ? runFrames[spriteNum] : idleImage;
        
        if (currentImage != null) {
            int drawWidth = tileSize * 2;
            int drawHeight = tileSize * 2;

            if (facingLeft) {
                // Mirror Flip: Draw the image starting from right-to-left
                g2.drawImage(currentImage, 
                             playerX + drawWidth, playerY, // Destination start X (shifted right), Y
                             playerX, playerY + drawHeight, // Destination end X, Y
                             0, 0,                          // Source image start X, Y
                             currentImage.getWidth(),       // Source image width
                             currentImage.getHeight(),      // Source image height
                             null);
            } else {
                // Normal Render: Facing right
                g2.drawImage(currentImage, playerX, playerY, drawWidth, drawHeight, null);
            }
        }

        // Render HUD / UI Overlay
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2.drawString("HP: [♥ ♥ ♥]", 20, 30);
        g2.drawString("STATUS: Dynamic Sprite Mirroring Active", 20, 50);
        
        g2.dispose();
    }
}