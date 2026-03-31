package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class Enemy extends Entity {
  GamePanel gamePanel;

  public boolean isMoving = false;
  public int targetX, targetY;

  public int dirX; // 1 (right), -1 (left), or 0
  public int dirY; // 1 (down), -1 (up), or 0
  public Color color;

  private Runnable intent = null;

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

    this.initiative = new Random().nextInt(9) + 1; // Initiative from 1 to 9

    this.maxHealth = 10;
    this.health = 10;
    this.minDamage = 1;
    this.maxDamage = 3;
  }

  @Override
  public void determineIntent(GamePanel gamePanel) {
    if (intent == null && !isMoving && !isDead) {

      // check if player is adjacent
      Player player = gamePanel.getPlayer();
      if (!player.isDead) {
        if ((x + gamePanel.tileSize == player.x && y == player.y)
            || (x - gamePanel.tileSize == player.x && y == player.y)
            || (x == player.x && y + gamePanel.tileSize == player.y)
            || (x == player.x && y - gamePanel.tileSize == player.y)) {
          intent = () -> this.attack(player);
          return; // end turn after deciding to attack
        }
      }

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

      if (targetEntity instanceof Player) {
        intent = () -> this.attack(targetEntity);
      } else if (targetEntity == null) {
        // It's null, so we move.
        this.targetX = newTargetX;
        this.targetY = newTargetY;
        intent = () -> isMoving = true;
      }
      // If targetEntity is an Enemy, do nothing.
    }
  }

  @Override
  public void executeAction(GamePanel gamePanel) {
    if (intent != null) {
      intent.run();
      intent = null;
    }
  }

  @Override
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

  @Override
  public void draw(Graphics2D g2) {
    if (!isDead) {
      int spriteSize = (int) (gamePanel.tileSize * 0.8);
      int offset = (gamePanel.tileSize - spriteSize) / 2;

      g2.setColor(color);
      g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);

      // Draw health bar
      g2.setColor(Color.RED);
      g2.fillRect(x + offset, y + offset - 5, spriteSize, 4);
      g2.setColor(Color.GREEN);
      int hpWidth = (int) (((double) health / maxHealth) * spriteSize);
      g2.fillRect(x + offset, y + offset - 5, hpWidth, 4);
    }
  }
}
