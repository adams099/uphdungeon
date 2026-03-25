package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy extends Entity {
    GamePanel gamePanel;
    
    public boolean isMoving = false;
    public int targetX, targetY;
    
    public int dirX; // 1 (right), -1 (left), or 0
    public int dirY; // 1 (down), -1 (up), or 0
    public Color color;

    public Enemy(GamePanel gamePanel, int startX, int startY, int dirX, int dirY, Color color) {
        this.gamePanel = gamePanel;
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;
        this.speed = 4;
        this.dirX = dirX;
        this.dirY = dirY;
        this.color = color;
    }
    
    public void takeTurn() {
        if (!isMoving) {
            targetX = x + (dirX * gamePanel.tileSize);
            targetY = y + (dirY * gamePanel.tileSize);
            
            // Simple bounds checking to reverse direction
            if (targetX < 0 || targetX + gamePanel.tileSize > gamePanel.screenWidth) {
                dirX *= -1;
                targetX = x + (dirX * gamePanel.tileSize);
            }
            if (targetY < 0 || targetY + gamePanel.tileSize > gamePanel.screenHeight) {
                dirY *= -1;
                targetY = y + (dirY * gamePanel.tileSize);
            }
            
            isMoving = true;
        }
    }
    
    public void update() {
        if (isMoving) {
            if (x < targetX) x += speed;
            if (x > targetX) x -= speed;
            if (y < targetY) y += speed;
            if (y > targetY) y -= speed;
            
            // Snap to target if very close to prevent jitter
            if (Math.abs(x - targetX) < speed && Math.abs(y - targetY) < speed) {
                x = targetX;
                y = targetY;
                isMoving = false;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fillRect(x, y, gamePanel.tileSize, gamePanel.tileSize);
    }
}