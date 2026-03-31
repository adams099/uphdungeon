package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import id.uphdungeon.KeyHandler;
import java.awt.Color;
import java.awt.Graphics2D;

public class Player extends Entity {
  KeyHandler keyH;

  public boolean isMoving = false;
  public int targetX, targetY;

  private Runnable intent = null;

  public Player(GamePanel gamePanel, KeyHandler keyH) {
    super(gamePanel);
    this.keyH = keyH;

    // align with grid
    this.x = gamePanel.tileSize * 2;
    this.y = gamePanel.tileSize * 2;

    this.speed = 4;
    this.initiative = 10; // Player has high initiative

    this.maxHealth = 20;
    this.health = 20;
    this.minDamage = 2;
    this.maxDamage = 5;

    this.targetX = this.x;
    this.targetY = this.y;
  }

  @Override
  public void update() {
    if (isMoving) {
      if (x < targetX) x += speed;
      if (x > targetX) x -= speed;
      if (y < targetY) y += speed;
      if (y > targetY) y -= speed;

      // snap to target if very close to prevent jitter
      if (Math.abs(x - targetX) < speed && Math.abs(y - targetY) < speed) {
        x = targetX;
        y = targetY;
        isMoving = false;
      }
    }
  }

  @Override
  public void determineIntent(GamePanel gamePanel) {
    if (intent == null && !isMoving && keyH.moveTriggered) {
      int nextX = x;
      int nextY = y;

      if (keyH.wasUpPressed) {
        nextY -= gamePanel.tileSize;
      }
      if (keyH.wasDownPressed) {
        nextY += gamePanel.tileSize;
      }
      if (keyH.wasLeftPressed) {
        nextX -= gamePanel.tileSize;
      }
      if (keyH.wasRightPressed) {
        nextX += gamePanel.tileSize;
      }

      // cancel contradicting directions
      if (keyH.wasUpPressed && keyH.wasDownPressed) nextY = y;
      if (keyH.wasLeftPressed && keyH.wasRightPressed) nextX = x;

      // only start initiative if player changed position
      if (nextX != x || nextY != y) {
        Entity targetEntity = gamePanel.getEntityAt(nextX, nextY);

        if (targetEntity != null) {
          // if enemy exists, attack enemy
          if (targetEntity instanceof Enemy) {
            intent = () -> this.attack(targetEntity);
          }
        } else {
          this.targetX = nextX;
          this.targetY = nextY;
          intent = () -> isMoving = true;
        }
      }

      keyH.consumeMove();
    }
  }

  @Override
  public void executeAction(GamePanel gamePanel) {
    if (intent != null) {
      intent.run();
      intent = null;
    }
  }

  public boolean hasIntent() {
    return intent != null;
  }

  @Override
  public void draw(Graphics2D g2) {
    if (!isDead) {
      int spriteSize = (int) (gamePanel.tileSize * 0.8);
      int offset = (gamePanel.tileSize - spriteSize) / 2;

      g2.setColor(Color.WHITE);
      g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);

      // health bar above player
      g2.setColor(Color.RED);
      g2.fillRect(x + offset, y + offset - 5, spriteSize, 4);
      g2.setColor(Color.GREEN);
      int hpWidth = (int) (((double) health / maxHealth) * spriteSize);
      g2.fillRect(x + offset, y + offset - 5, hpWidth, 4);
    }

    drawIndicators(g2);
  }
}
