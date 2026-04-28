package id.uphdungeon.entity;

import id.uphdungeon.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public abstract class Enemy extends Entity {
  public boolean isMoving = false;
  public int targetX, targetY;

  public int dirX; // kanan: 1, kiri: -1, netral: 0
  public int dirY; // bawah: 1, atas: -1, netral: 0
  public Color color;

  private Runnable intent = null;

  public Enemy(GamePanel gamePanel, int startX, int startY, int dirX, int dirY, Color color,
      int maxHealth, int minDamage, int maxDamage) {
    super(gamePanel);
    this.x = startX;
    this.y = startY;
    this.targetX = startX;
    this.targetY = startY;
    // comment dulu, handled di super biar lebih cepet
    // this.speed = 4;
    this.dirX = dirX;
    this.dirY = dirY;
    this.color = color;

    // initiative dari 1 ke 9
    this.initiative = new Random().nextInt(9) + 1;

    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.minDamage = minDamage;
    this.maxDamage = maxDamage;
  }

  public abstract int getExpReward();

  @Override
  public void determineIntent(GamePanel gamePanel) {
    if (intent == null && !isMoving && !isDead) {
      // cek player di sebelah musuh ini
      Player player = gamePanel.getPlayer();
      if (!player.isDead) {
        int deltaX = Math.abs(player.x - this.x);
        int deltaY = Math.abs(player.y - this.y);

        if (deltaX <= gamePanel.tileSize && deltaY <= gamePanel.tileSize
            && (deltaX != 0 || deltaY != 0)) {
          intent = () -> this.attack(player);
          return;
        }
      }

      int newTargetX = x + (dirX * gamePanel.tileSize);
      int newTargetY = y + (dirY * gamePanel.tileSize);

      // cek arah sebaliknya
      if (newTargetX < 0 || newTargetX + gamePanel.tileSize > gamePanel.screenWidth) {
        dirX *= -1;
        newTargetX = x + (dirX * gamePanel.tileSize);
      }
      if (newTargetY < 0 || newTargetY + gamePanel.tileSize > gamePanel.screenHeight) {
        dirY *= -1;
        newTargetY = y + (dirY * gamePanel.tileSize);
      }

      // collission check at target location
      Entity targetEntity = gamePanel.getEntityAt(newTargetX, newTargetY);

      if (targetEntity instanceof Player) {
        intent = () -> this.attack(targetEntity);
      } else if (targetEntity == null) {
        // grid kosong, bisa gerak
        this.targetX = newTargetX;
        this.targetY = newTargetY;
        intent = () -> isMoving = true;
      }
      // kalo di grid target sesama musuh, ga ngapa2in
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
  public void updateFading() {
    if (isFading) {
      alpha -= 0.02f;
      if (alpha < 0) {
        alpha = 0;
        isFading = false;
      }
    }
  }

  @Override
  public void draw(Graphics2D g2) {
    if (!isDead || isFading) {
      if (isFading) {
        g2.setComposite(
            java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
      }

      int spriteSize = (int) (gamePanel.tileSize * 0.8);
      int offset = (gamePanel.tileSize - spriteSize) / 2;

      g2.setColor(color);
      g2.fillRect(x + offset, y + offset, spriteSize, spriteSize);

      if (!isDead) {
        // health bar buat musuh hidup
        g2.setColor(Color.RED);
        g2.fillRect(x + offset, y + offset - 5, spriteSize, 4);
        g2.setColor(Color.GREEN);
        int hpWidth = (int) (((double) health / maxHealth) * spriteSize);
        g2.fillRect(x + offset, y + offset - 5, hpWidth, 4);
      }

      if (isFading) {
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1f));
      }
    }
    drawIndicators(g2);
  }
}
