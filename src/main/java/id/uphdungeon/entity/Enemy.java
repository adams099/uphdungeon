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
        
        this.maxHealth = 10;
        this.health = 10;
        this.minDamage = 1;
        this.maxDamage = 3;
    }
    
    public void takeTurn() {
        if (!isMoving && !isDead) {
            int newTargetX = x + (dirX * gamePanel.tileSize);
            int newTargetY = y + (dirY * gamePanel.tileSize);
            
            // Simple bounds checking to reverse direction
            if (newTargetX < 0 || newTargetX + gamePanel.tileSize > gamePanel.screenWidth) {
                dirX *= -1;
                newTargetX = x + (dirX * gamePanel.tileSize);
            }
            if (newTargetY < 0 || newTargetY + gamePanel.tileSize > gamePanel.screenHeight) {
                dirY *= -1;
                newTargetY = y + (dirY * gamePanel.tileSize);
            }
            
            // Check for collision at target location
            Entity targetEntity = gamePanel.getEntityAt(newTargetX, newTargetY);
            
            if (targetEntity != null) {
                if (targetEntity instanceof Player) {
                    this.attack(targetEntity);
                } else if (targetEntity instanceof Enemy) {
                    // Turn around if we bump into another enemy
                    dirX *= -1;
                    dirY *= -1;
                }
                
                // Do not move into occupied tile
                targetX = x;
                targetY = y;
            } else {
                targetX = newTargetX;
                targetY = newTargetY;
                isMoving = true;
            }
        }
    }
    
    public void update() {
        if (isMoving && !isDead) {
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
        if (!isDead) {
            g2.setColor(color);
            g2.fillRect(x, y, gamePanel.tileSize, gamePanel.tileSize);
            
            // Draw health bar
            g2.setColor(Color.RED);
            g2.fillRect(x, y - 5, gamePanel.tileSize, 4);
            g2.setColor(Color.GREEN);
            int hpWidth = (int) (((double) health / maxHealth) * gamePanel.tileSize);
            g2.fillRect(x, y - 5, hpWidth, 4);
        }
    }
}