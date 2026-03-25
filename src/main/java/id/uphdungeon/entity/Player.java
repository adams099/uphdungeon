package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.KeyHandler;
import java.awt.Color;
import java.awt.Graphics2D;

public class Player extends Entity {
    GamePanel gamePanel;
    KeyHandler keyH;
    
    public boolean isMoving = false;
    public int targetX, targetY;

    public Player(GamePanel gamePanel, KeyHandler keyH) {
        this.gamePanel = gamePanel;
        this.keyH = keyH;
        
        // Start aligned to the grid
        this.x = gamePanel.tileSize * 2;
        this.y = gamePanel.tileSize * 2;
        this.speed = 4;
        
        this.maxHealth = 20;
        this.health = 20;
        this.minDamage = 2;
        this.maxDamage = 5;
        
        this.targetX = this.x;
        this.targetY = this.y;
    }
    
    public void update() {
        if (!isMoving) {
            // Check for directional input to initiate a turn on key up
            if (keyH.moveTriggered) {
                targetX = x;
                targetY = y;
                
                if (keyH.wasUpPressed) {
                    targetY -= gamePanel.tileSize;
                }
                if (keyH.wasDownPressed) {
                    targetY += gamePanel.tileSize;
                }
                if (keyH.wasLeftPressed) {
                    targetX -= gamePanel.tileSize;
                }
                if (keyH.wasRightPressed) {
                    targetX += gamePanel.tileSize;
                }
                
                // Cancel out opposing directions just in case
                if (keyH.wasUpPressed && keyH.wasDownPressed) targetY = y;
                if (keyH.wasLeftPressed && keyH.wasRightPressed) targetX = x;

                // Only move if we actually changed position
                if (targetX != x || targetY != y) {
                    Entity targetEntity = gamePanel.getEntityAt(targetX, targetY);
                    
                    if (targetEntity != null) {
                        // There is an entity here, so we attack instead of moving
                        if (targetEntity instanceof Enemy) {
                            this.attack(targetEntity);
                        }
                        
                        // Prevent moving into occupied tile
                        targetX = x;
                        targetY = y;
                        
                        // Attacking takes a turn
                        gamePanel.advanceTurn();
                    } else {
                        // Empty tile, move
                        isMoving = true;
                        
                        // Once player commits to an action, advance the game turn
                        gamePanel.advanceTurn();
                    }
                }
                
                keyH.consumeMove();
            }
        } else {
            // Animate moving towards the target tile
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
        
        // Sprite animations can be updated here independently of grid movement
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, gamePanel.tileSize, gamePanel.tileSize);
        
        // Draw health bar above player
        g2.setColor(Color.RED);
        g2.fillRect(x, y - 5, gamePanel.tileSize, 4);
        g2.setColor(Color.GREEN);
        int hpWidth = (int) (((double) health / maxHealth) * gamePanel.tileSize);
        g2.fillRect(x, y - 5, hpWidth, 4);
    }
}